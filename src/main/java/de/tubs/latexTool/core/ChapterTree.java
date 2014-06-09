package de.tubs.latexTool.core;


import de.tubs.latexTool.core.entrys.*;
import de.tubs.latexTool.core.util.Abbreviation;
import de.tubs.latexTool.core.util.Misc;
import de.tubs.latexTool.core.util.ValueComparator;

import java.util.*;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Diese Klasse verwaltet alle Hierarchien eines Latex Dokuments
 */
public class ChapterTree implements IPosition {

  private static final Logger sLog = Logger.getLogger(ChapterTree.class.getName());
  /**
   * Der Bezeichner des Knotens
   */
  private final Headline mHeadline;
  /**
   * Der Text Inhalt
   */
  private final List<Text> mLatexTexts;
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
   * Die Unterknoten
   */
  private final List<ChapterTree> mSubChapters;
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
   * @param partList Liste der noch zu bearbeiteten Knotenkandidaten
   * @param headline Name des Knotens
   * @param content  Inhalt des Dokuments
   */
  private ChapterTree(int level, int start, int offset, List<Command> partList, Headline headline, StringBuilder content, ThreadPoolExecutor threadPool) {
    mLevel = level;
    mStart = start + offset;
    mHeadline = headline;
    if (mHeadline != null) {
      mHeadline.setChapterTree(this);
    }
    mSubChapters = new LinkedList<>();
    mParagraphs = new LinkedList<>();
    mLatexTexts = new LinkedList<>();

    if (!partList.isEmpty()) {
      mRaw = content.substring(start, partList.get(0).getStart() - 1 < 0 ? 0 : partList.get(0).getStart() - 1);
    } else {
      mRaw = content.substring(start);
    }

    Command part;
    int newLevel;
    String partName;
    while (!partList.isEmpty()) {
      part = partList.get(0);

      partName = part.getName();

      if (partName.endsWith("*")) {
        partName = partName.substring(0, partName.length() - 1);
      }

      newLevel = Api.settings().getParts().get(partName);

      // größeres mLevel (also tochter)
      if (level < newLevel) {
        partList.remove(0);
        mSubChapters.add(new ChapterTree(newLevel, part.getEnd() + 1, offset, partList, new Headline(part), content, threadPool));

      } else {
        // gleiches oder kleineres mLevel (also Schwester oder Tante)
        break;
      }
    }

    threadPool.execute(new ChapterTreeWorker());
    threadPool.execute(new ChapterTreeWorkerLatex());
  }

  /**
   * Analysiert das Dokument und baut den Baum
   */
  static ChapterTree build() {
    ChapterTree root = null;

    final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(8, 8, 10, TimeUnit.SECONDS, new LinkedTransferQueue<Runnable>());
    Timer timer = new Timer(true);
    if (Api.settings().isVerbose()) {
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          String s = String.format("ChapterTree %d threads are running, (%d / %4$d) are completed and (%d  / %4$d) are queued", threadPool.getActiveCount(), threadPool.getCompletedTaskCount(), threadPool.getQueue().size(), threadPool.getTaskCount());
          if (sLog.isLoggable(Level.FINEST)) {
            sLog.finest(s);
          }
          String t = String.format("ChapterTree: %3d%%", threadPool.getTaskCount() <= 0 ? 0 : threadPool.getCompletedTaskCount() * 100 / threadPool.getTaskCount());
          if (sLog.isLoggable(Level.FINER)) {
            sLog.finer(t);
          }
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

      String partsString = Misc.iterableToString(new LinkedList<>(parts.keySet()), true, false);

      List<Command> partList = Command.getCommands(String.format("%s%s", partsString, "(?<Hidden>\\*)?"), content.toString());

      // der Root Knoten hat die Wertigkeit -20, somit es darf keinen anderen Knoten geben mit einer Wertigkeit von weniger als -19 !
      root = new ChapterTree(-20, 0, offset, partList, null, content, threadPool);

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
    tex.allLatexTexts().addAll(mLatexTexts);

    for (Paragraph paragraph : mParagraphs) {
      tex.allTexts().addAll(paragraph.getTexts());
    }

    if (mHeadline != null) {
      tex.allHeadlines().add(mHeadline);
    }

    for (ChapterTree child : mSubChapters) {
      child.buildLists(tex);
    }
    //System.out.println("test");
  }

  /**
   * Gibt alle Unterknoten zurück
   *
   * @return die Unterknoten
   */
  public List<ChapterTree> child() {
    return mSubChapters;
  }

  public String getContent() {
    return mContent;
  }

  /**
   * Gibt den Namen des Knotens zurück
   *
   * @return
   */
  public Headline getHeadline() {
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
    if (mHeadline == null) {
      return "root";
    }
    return mHeadline.toString();
  }

  /**
   * Analysiert den Rohtext des Knotens um die Sätze zu finden
   */
  private class ChapterTreeWorker implements Runnable {
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
        content = Abbreviation.masking(content);
        assert length == content.length();
      }

      content = Misc.sortWhitespaces(content, ".!?");
      assert length == content.length();

      // Absätze finden usw

      mContent = content;

      String[] cParagraphs = content.split("\n\n");

      for (String cParagraph : cParagraphs) {
        Paragraph paragraph = new Paragraph(ChapterTree.this);
        List<String> sentencesString = Misc.getSentences(cParagraph);

        int start = 0;
        int index;
        Text textEntry;

        for (String sentence : sentencesString) {
          sentence = sentence.trim();
          index = content.indexOf(sentence, start);
          if (Api.settings().isUseAbbreviationsEscaping()) {
            textEntry = new Text(Abbreviation.unmasking(sentence), paragraph, index, mStart);
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

  /**
   * Analysiert den Rohtext des Knotens um die Sätze (inclusive Latex Befehlen) zu finden
   */
  private class ChapterTreeWorkerLatex implements Runnable {
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

      if (Api.settings().isUseAbbreviationsEscaping()) {
        content = Abbreviation.masking(content);
        assert length == content.length();
      }

      List<String> sentencesString = Misc.getSentences(content);

      int start = 0;
      int index;
      Text textEntry;

      for (String sentence : sentencesString) {
        sentence = sentence.trim();
        index = content.indexOf(sentence, start);
        if (Api.settings().isUseAbbreviationsEscaping()) {
          textEntry = new Text(Abbreviation.unmasking(sentence), index, mStart);
        } else {
          textEntry = new Text(sentence, index, mStart);
        }
        mLatexTexts.add(textEntry);
        start = index + sentence.length() - 1;
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
