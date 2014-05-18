package de.tubs.latexTool.core;


import de.tubs.latexTool.core.entrys.*;
import de.tubs.latexTool.core.util.Abbreviation;
import de.tubs.latexTool.core.util.Misc;
import de.tubs.latexTool.core.util.ValueComparator;

import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse verwaltet alle Hierarchien eines Latex Dokuments
 */
public class DocumentTree implements IPosition {

  private static final Logger sLog = Logger.getLogger(DocumentTree.class.getName());

  /**
   * Die Unterknoten
   */
  private final List<DocumentTree> mChildren;
  /**
   * Der Bezeichner des Knotens
   */
  private final String mHeadline;
  /**
   * Gibt die Wertigkeit des Knotens im Dokument an, z.B. Part, Chapter, Section ...
   */
  private final int mLevel;
  /**
   * Alle Paragrafen des Knotens
   */
  private final List<Paragraph> mParagraphs;
  /**
   * Der Rohtext des Knotens
   */
  private final String mRaw;
  /**
   * Gibt die Startposition (Zeichen) im Zusammengesetzten Latex Dokuments an
   */
  private final int mStart;
  /**
   * der Content nach dem alles entfernt wurde
   */
  private String mContent;

  /**
   * Erstellt einen neuen Knoten
   *
   * @param level    die Wertigkeit
   * @param start    Anfang des Knotens im Dokument (Start ->/section{mName})
   * @param offset   die Verschiebung (/section{mName}<- offset)
   * @param list     Liste der noch zu zu bearbeitenen Knotenkandidaten
   * @param headline Name des Knotens
   * @param content  Inhalt des Dokuments
   */
  private DocumentTree(int level, int start, int offset, LinkedList<MatchResult> list, String headline, StringBuilder content, ThreadPoolExecutor threadPool) {
    mLevel = level;
    mStart = start + offset;
    mHeadline = headline;
    mChildren = new LinkedList<>();
    mParagraphs = new LinkedList<>();

    if (!list.isEmpty()) {
      mRaw = content.substring(start, list.getFirst().start() - 1 < 0 ? 0 : list.getFirst().start() - 1);
    } else {
      mRaw = content.substring(start);
    }

    MatchResult matchResult;
    int newLevel;
    while (!list.isEmpty()) {
      matchResult = list.getFirst();
      newLevel = Api.settings().getParts().get(matchResult.group(1));

      // groesseres mLevel (also tochter)
      if (level < newLevel) {
        list.pollFirst();
        // richtige nummer einfuegen
        String cheadline;
        if (matchResult.group(5) != null) {
          cheadline = matchResult.group(5);
        } else {
          cheadline = matchResult.group(6);
        }
        mChildren.add(new DocumentTree(newLevel, matchResult.end(), offset, list, cheadline, content, threadPool));
      } else {
        // gleiches oder kleineres mLevel (also schwester oder tante)
        break;
      }
    }

    threadPool.execute(new DocumentTreeWorker());
  }

  /**
   * Analysiert das Dokument und baut den Baum
   */
  static DocumentTree build() {
    DocumentTree root = null;

    final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(8, 8, 10, TimeUnit.SECONDS, new LinkedTransferQueue<Runnable>());
    Timer timer = new Timer(true);
    if (Api.settings().isVerbose()) {
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          String s = String.format("DocumentTree %d threads are running, (%d / %4$d) are completed and (%d  / %4$d) are queued", threadPool.getActiveCount(), threadPool.getCompletedTaskCount(), threadPool.getQueue().size(), threadPool.getTaskCount());
          sLog.finest(s);
          String t = String.format("DocumentTree: %3d%%", threadPool.getTaskCount() <= 0 ? 0 : (threadPool.getCompletedTaskCount() * 100) / threadPool.getTaskCount());
          sLog.finer(t);
          System.out.println(t);
        }
      }, 100, 500);
    }

    try {
      int offset;
      StringBuilder content;

      if (!Api.settings().isHasNoDocumentEnv()) {
        Environment document = Api.getEnvironments("document").get(0);
        offset = document.getContentStart();
        content = new StringBuilder(document.getContent());
      } else {
        offset = 0;
        content = new StringBuilder(Api.getRawContent());
      }

      Map<String, Integer> partsTmp = Api.settings().getParts();

      ValueComparator valueComparator = new ValueComparator(partsTmp);
      Map<String, Integer> parts = new TreeMap<>(valueComparator);
      parts.putAll(partsTmp);

      String partsString = Misc.iterableToString(new LinkedList<>(parts.keySet()), true);

      Pattern partsPattern = Pattern.compile(String.format("\\\\(?<Part>%s)(?<Hidden>\\*)?\\s*(?<Titles>(?<ShortTitleAll>\\[(?<ShortTitle>.*?)\\])?\\{(?<LongTitle>.*?)\\})", partsString), Pattern.DOTALL);

      // direkt auf dem String arbeiten, sonst erstellt er jedes mal einen neuen
      Matcher matcher = partsPattern.matcher(content.toString());
      LinkedList<MatchResult> list = new LinkedList<>();
      while (matcher.find()) {
        list.add(matcher.toMatchResult());
      }

      // der Rootknoten hat die Wertigkeit -20, somit es darf keinen anderen Knoten geben mit einer Wertigkeit von weniger als -19 !
      root = new DocumentTree(-20, 0, offset, list, "root", content, threadPool);

      threadPool.shutdown();
      try {
        if (!threadPool.awaitTermination(10, TimeUnit.MINUTES)) {
          sLog.severe("The documentTree need more than 10 minutes to build the tree!");

          timer.cancel();
        }
      } catch (InterruptedException e) {
        sLog.severe("The documentTree build process was interrupted!");
        //System.err.println(e.getMessage());
        timer.cancel();
      }
      timer.cancel();
    } catch (IndexOutOfBoundsException e) {
      sLog.severe("The document has no \"Document\" environment, use \"-nd\" for all content of the tex files");
      //System.err.println(e.getMessage());
    }
    return root;
  }

  /**
   * Baut die 3 Listen
   */
  void buildLists(Tex tex) {

    tex.allParagraphs().addAll(mParagraphs);

    for (Paragraph paragraph : mParagraphs) {
      tex.allTexts().addAll(paragraph.getTexts());
    }

    if (mHeadline != null && !mHeadline.equals("root")) {
      tex.allHeadlines().add(new Text(mHeadline, new Paragraph(this), 0, mStart));
    }

    for (DocumentTree child : mChildren) {
      child.buildLists(tex);
    }
    //System.out.println("test");
  }

  /**
   * Gibt alle Unterknoten zurück
   *
   * @return die Unterknoten
   */
  public List<DocumentTree> child() {
    return mChildren;
  }

  public String getContent() {
    return mContent;
  }

  /**
   * Gibt den Namen des Knotents zurück
   *
   * @return
   */
  public String getHeadline() {
    return mHeadline;
  }

  /**
   * Gibt die Wertigkeit des Knotens zurück
   *
   * @return
   */
  public int getLevel() {
    return mLevel;
  }

  /**
   * Gibt alle direkten Paragraphen des Knoten zurück
   *
   * @return
   */
  public List<Paragraph> getParagraphs() {
    return mParagraphs;
  }

  /**
   * Gibt alle direkten Texte des Knoten zurück
   *
   * @return
   */
  public List<Text> getTexts() {
    List<Text> ret = new LinkedList<>();
    for (Paragraph paragraph : mParagraphs) {
      ret.addAll(paragraph.getTexts());
    }
    return ret;
  }

  @Override
  public String toString() {
    return mHeadline;
  }

  /**
   * Analysiert den Rohtext des Knotens um die Sätze zu finden
   */
  private class DocumentTreeWorker implements Runnable {
    @Override
    public void run() {

      String content = mRaw;

      int length = content.length();

      content = content.replaceAll("\\t", " ");
      assert length == content.length();
      content = Misc.noSingleLF(content);
      assert length == content.length();
      content = Misc.sortNewline(content);
      assert length == content.length();
      content = Misc.maskingLatex(content, true);
      assert length == content.length();
      content = Misc.maskingEnvironment(content);
      assert length == content.length();
      content = Misc.removeLatex(content);
      assert length == content.length();

      if (Api.settings().isUseAbbreviationsEscaping()) {
        content = Abbreviation.maskingAbb(content);
        assert length == content.length();
      }

      content = Misc.sortWhitespaces(content, ".!?");
      assert length == content.length();

      // Absätze finden usw

      mContent = content;

      String[] cParagraphs = content.split("\n\n");

      for (String cParagraph : cParagraphs) {
        Paragraph paragraph = new Paragraph(DocumentTree.this);
        List<String> sentencesString = Misc.getSentences(cParagraph, false);

        int start = 0;
        int index;
        Text textEntry;

        for (String sentence : sentencesString) {
          sentence = sentence.trim();
          index = content.indexOf(sentence, start);
          if (Api.settings().isUseAbbreviationsEscaping()) {
            textEntry = new Text(Abbreviation.unMaskingAbb(sentence), paragraph, index, mStart);
          } else {
            textEntry = new Text(sentence, paragraph, index, mStart);
          }
          if (textEntry.isSentence()) {
            paragraph.getTexts().add(textEntry);
          }
          start = index + sentence.length() - 1;
        }
        if (!paragraph.getTexts().isEmpty()) {
          mParagraphs.add(paragraph);
        }
      }
    }
  }

  @Override
  public int getStart() {
    return mStart;
  }


  /**
   * Gibt die Position des Knoten im Dokument zurück
   *
   * @return der Position Eintrag
   */
  public Position getPosition() {
    return Api.getPosition(mStart);
  }


  @Override
  public int getEnd() {
    return mStart + mRaw.length();
  }


  @Override
  public Position getEndPosition() {
    return Api.getPosition(getEnd());
  }


}
