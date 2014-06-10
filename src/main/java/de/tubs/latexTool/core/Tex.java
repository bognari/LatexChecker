package de.tubs.latexTool.core;

import de.tubs.latexTool.core.entrys.*;
import de.tubs.latexTool.core.util.Misc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse ist für die Verwaltung der Tex Datei, in rohform und zusammengesetzt, verantwortlich
 */
public class Tex {

  // für den Filter
  private static final Pattern sComment = Pattern.compile("(?<!\\\\)%.*$");
  private static final Pattern sInclude = Pattern.compile("(?<!\\\\)\\\\include\\s*\\{(?<Path>.*?)\\}");
  private static final Pattern sInput = Pattern.compile("(?<!\\\\)\\\\input\\s*\\{(?<Path>.*?)\\}");
  private static final String sSBadCommand = "(?<!\\\\)(?<BeginCom>\\\\%s\\s*(?<Klammer>\\S))(?<ContentCom>.*?)(?<EndCom>\\k<Klammer>)";
  private static final String sSBadEnvEnd = "^(?<Content>.*)(?<!\\\\)(?<End>\\\\end\\s*\\{(?<Environment>%s)\\})";
  private static final String sSBadEnvStart = "(?<!\\\\)(?<Begin>\\\\begin\\s*\\{(?<Environment>%s)\\})(?<Content>.*)$";
  private static final String sSBadEnvironment = "(?<!\\\\)(?<BeginEnv>\\\\begin\\s*\\{(?<Environment>%s)\\})(?<ContentEnv>.*?)(?<EndEnv>\\\\end\\s*\\{\\k<Environment>\\})";
  private static final String sSBad = String.format("(?<Env>%s)|(?<Com>%s)", sSBadEnvironment, sSBadCommand);
  // für den Filter
  private final Object lock = new Object();
  /**
   * Liste aller Überschriften im Dokument
   */
  private final List<Headline> mAllHeadlines = new LinkedList<>();
  /**
   * Liste aller Sätze (inklusive Latex) des Dokuments
   */
  private final List<Text> mAllLatexTexts = new LinkedList<>();
  /**
   * Liste aller Paragrafen des Dokuments
   */
  private final List<Paragraph> mAllParagraphs = new LinkedList<>();
  /**
   * Liste aller Sätze des Dokuments
   */
  private final List<Text> mAllTexts = new LinkedList<>();
  /**
   * der Charset der Latex Dokumente, alle Dokumenten müssten den gleichen Charset benutzen !
   */
  private final Charset mCharset;
  /**
   * Der zusammengesetzte Inhalt aller Tex Dateien ohne die Kommentare
   */
  private final String mContent;
  /**
   * Der Pfad zur "Root"-Datei
   */
  private final Path mFile;
  private final Logger mLog = Logger.getLogger(Tex.class.getName());
  private final Pattern mPBad;
  private final Pattern mPBadEnvStart;
  /**
   * Achtung beginnen bei 0 !!!!!!!!!!!!!!!!!!!
   * long würde zwar mehr speicher bringen aber der matcher gibt nur int zurück
   * somit darf eine Tex Datei max Integer.Max Chars beinhalten oO
   */
  private final TreeMap<Integer, Position> mPositions;
  private volatile ChapterTree mChapterTreeRoot;

  /**
   * Erstellt das Tex Objekt
   */
  public Tex() {
    mPBad = Pattern.compile(String.format(sSBad, Misc.iterableToString(Api.settings().getBadEnv(), true, false), Misc.iterableToString(Api.settings().getBadCom(), true, false)));
    mPBadEnvStart = Pattern.compile(String.format(sSBadEnvStart, Misc.iterableToString(Api.settings().getBadEnv(), true, false)));

    mFile = Paths.get(Api.settings().getSource());
    mCharset = Charset.forName(Api.settings().getCharset());
    int level = 10;
    mPositions = new TreeMap<>();
    StringBuilder content = new StringBuilder(75000);
    loadFile(mFile, content, level);
    mContent = Misc.removeReNew(content.toString());
  }

  /**
   * nur zum Testen
   *
   * @param content
   */
  public Tex(String content) {
    mContent = content;
    mPositions = new TreeMap<>();
    mCharset = null;
    mFile = null;
    mPBad = null;
    mPBadEnvStart = null;
  }

  /**
   * Gibt alle Überschriften des Dokuments zurück
   *
   * @return alle Überschriften
   */
  List<Headline> allHeadlines() {
    getChapterTreeRoot();
    return mAllHeadlines;
  }

  /**
   * Gibt den ChapterTree des Dokuments zurück
   *
   * @return ChapterTree
   */
  ChapterTree getChapterTreeRoot() {
    if (mChapterTreeRoot == null) {
      synchronized (lock) {
        if (mChapterTreeRoot == null) {
          mLog.fine("getChapterTree -> build");
          mChapterTreeRoot = ChapterTree.build();
          if (mChapterTreeRoot != null) {
            mAllHeadlines.clear();
            mAllParagraphs.clear();
            mAllTexts.clear();
            mChapterTreeRoot.buildLists(this);
          } else {
            mLog.warning("error while creating the document tree");
          }
        }
      }
    }
    return mChapterTreeRoot;
  }

  /**
   * Gibt alle Sätze (inklusive Latex) des Dokuments zurück
   *
   * @return alle Sätze
   */
  List<Text> allLatexTexts() {
    getChapterTreeRoot();
    return mAllLatexTexts;
  }

  /**
   * Gibt alle Paragrafen des Dokuments zurück
   *
   * @return alle Paragrafen
   */
  List<Paragraph> allParagraphs() {
    getChapterTreeRoot();
    return mAllParagraphs;
  }

  /**
   * Gibt alle Sätze des Dokuments zurück
   *
   * @return alle Sätze
   */
  List<Text> allTexts() {
    getChapterTreeRoot();
    return mAllTexts;
  }

  /**
   * Gibt eine Liste aller Gefunden Commands zurück
   *
   * @param command regex
   * @return
   */
  public List<Command> getCommands(String command) {
    return Command.getCommands(command, mContent);
  }

  /**
   * gibt den Inhalt zurück
   *
   * @return
   */
  public String getContent() {
    return mContent;
  }

  /**
   * Gibt eine Liste
   *
   * @param environment regex
   * @return
   */
  public List<Environment> getEnvironments(String environment) {
    return Environment.getEnvironments(environment, mContent);
  }

  /**
   * Gibt die Position zurück
   *
   * @param offset position des "Char" im Content String
   * @return
   */
  Position getPosition(int offset) {
    return mPositions.floorEntry(offset).getValue();
  }

  /**
   * Liest die Datei ein und fügt den Inhalt an die richtige Position
   *
   * @param file
   * @param stringBuilder pointer zum Content StringBuilder (nicht mit dem String verwechseln)
   * @param level
   */
  private void loadFile(Path file, StringBuilder stringBuilder, int level) {
    if (level < 1) {
      return;
    }

    if (mLog.isLoggable(Level.FINE)) {
      mLog.fine(String.format("loadFile start reading file = %s", file));
    }

    try {
      List<String> textLines = Files.readAllLines(file, mCharset);
      int line = 1;

      Matcher matcher;

      LatexFilter filter = new LatexFilter();

      for (String textLine : textLines) {

        // do some magic... lawl
        textLine = filter.filter(textLine);

        matcher = sInput.matcher(textLine);

        String path;
        while (matcher.find()) {
          path = matcher.group("Path");
          if (!path.endsWith(".tex")) {
            path += ".tex";
          }
          loadFile(Paths.get(mFile.getParent().toString(), path), stringBuilder, level - 1);
        }

        textLine = matcher.replaceAll("");

        matcher = sInclude.matcher(textLine);

        while (matcher.find()) {
          path = matcher.group("Path");
          if (!path.endsWith(".tex")) {
            path += ".tex";
          }
          loadFile(Paths.get(mFile.getParent().toString(), path), stringBuilder, 1);
        }

        textLine = matcher.replaceAll("");

        mPositions.put(stringBuilder.length(), new Position(file, line++));
        stringBuilder.append(textLine).append(System.lineSeparator());
      }
      if (mLog.isLoggable(Level.FINE)) {
        mLog.fine(String.format("loadFile finish reading file = %s", file));
      }
    } catch (IOException e) {
      mLog.throwing(Tex.class.getName(), "loadFile", e);
    }
  }

  @Override
  public String toString() {
    return mFile.toString();
  }

  /**
   * Filtert alles "böse" aus der Tex Datei, z.b. Kommentare, listings Umgebungen usw.
   */
  class LatexFilter {
    private Pattern mBadEnvEnd;

    String filter(String input) {
      StringBuilder stringBuilder = new StringBuilder();
      int i = 0;

      // befinden uns in einer Umgebung die alles Kaputt macht
      if (mBadEnvEnd != null) {
        Matcher badEnvEnd = mBadEnvEnd.matcher(input);
        // testen ob diese geschlossen wird
        if (badEnvEnd.find()) {
          // masking
          stringBuilder.append(masking(badEnvEnd.group("Content")));
          // das ende einfügen
          stringBuilder.append(badEnvEnd.group("End"));
          i = badEnvEnd.end();

          mBadEnvEnd = null;
          // wenn nicht, dann gebe den Inhalt maskiert usw zurück
        } else {
          return removeComment(masking(input));
        }
      } else {
        // sind im normalen latex modus
        //if (mBadEnvEnd == null) {
        Matcher bad = mPBad.matcher(input);

        // testen ob es eine Umgebung oder Befehl gibt die den Latex Modus verlässt
        while (bad.find()) {
          // anfang einfügen
          stringBuilder.append(input.substring(i, bad.start()));
          // es war eine umgebung
          if (bad.group("Env") != null) {
            // \begin{bla} usw einfügen, hier wird [] ignoriert !
            stringBuilder.append(bad.group("BeginEnv"));
            // content maskieren
            stringBuilder.append(masking(bad.group("ContentEnv")));
            // \end{bla} einfügen
            stringBuilder.append(bad.group("EndEnv"));
          }
          // eigentlich reicht hier ein else aber wayne
          // es war ein befehl
          if (bad.group("Com") != null) {
            // \bla<! oder |> einfügen
            stringBuilder.append(bad.group("BeginCom"));
            // content maskieren
            stringBuilder.append(masking(bad.group("ContentCom")));
            // <! oder |> einfügen
            stringBuilder.append(bad.group("EndCom"));
          }
          i = bad.end();
        }

        Matcher badEnvStart = mPBadEnvStart.matcher(input);

        // suchen ob eine latex modus verlassende umgebung begonnen wird
        if (badEnvStart.find()) {
          // anfang einfügen
          stringBuilder.append(input.substring(i, badEnvStart.start()));
          // \begin{bla}
          stringBuilder.append(badEnvStart.group("Begin"));
          // content maskieren
          stringBuilder.append(masking(badEnvStart.group("Content")));

          // meta daten setzen
          mBadEnvEnd = Pattern.compile(String.format(sSBadEnvEnd, badEnvStart.group("Environment")));
          return removeComment(stringBuilder.toString());
        }
      }
      stringBuilder.append(input.substring(i));

      String ret = removeComment(stringBuilder.toString());

      if (Api.settings().isConverting()) {
        ret = convert(ret);
      }

      return ret;
    }

    String masking(String string) {

      //return string.replaceAll(Pattern.quote("\\"), "\\\\\\\\").replaceAll(Pattern.quote("{"), "\\\\{").replaceAll(Pattern.quote("}"), "\\\\}").replaceAll(Pattern.quote("%"), "\\\\%");
      return string.replaceAll("(\\\\|\\{|\\}|%)", "\\\\$1");
    }

    String removeComment(String string) {
      return sComment.matcher(string).replaceFirst("");
    }

    String convert(String string) {
      Map<String, String> convertTable = Api.settings().getConvertingTable();
      for (String from : convertTable.keySet()) {
        string = string.replaceAll(Pattern.quote(from), Matcher.quoteReplacement(convertTable.get(from)));
      }
      return string;
    }
  }
}