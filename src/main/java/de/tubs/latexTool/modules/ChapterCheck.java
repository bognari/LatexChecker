package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.DocumentTree;
import de.tubs.latexTool.core.entrys.Result;

import java.util.IllegalFormatException;

/**
 * Testet ob eine Dokument zu viele verschachtelte Kapitel hat und ob jedes Kapitel zwischen min und max Unterkapitel
 * Besitzt.
 * Wenn ein Kapitel keine Unterkapitel hat, dann ist er vom min ausgeschlossen.
 * Testet ob jedes Kapitel zwischen min und max "Sätze" besitzt.
 * Eingrenzungen mittels FromLevel und ToLevel möglich.
 */
public class ChapterCheck extends Module {

  private static final String sMsg = "%1$s %2$s %3$d %4$s (%5$d)";
  /**
   * "MsgChapters" = "Text" <p></p>
   * Der Nachrichten Prototyp im Falle zu großer Kapiteltiefe
   * <p></p> default: "%1$s %2$s %3$d %4$s (%5$d)"
   */
  @Expose
  @SerializedName("MsgChapters")
  private String cMsgChapters = sMsg;
  /**
   * "MsgSentences" = "Text" <p></p>
   * Der Nachrichten Prototyp im Falle von zu vielen oder zu wenigen Sätzen pro Abschnitt
   * <p></p> default: "%1$s %2$s %3$d %4$s (%5$d)"
   */
  @Expose
  @SerializedName("MsgSentences")
  private String cMsgSentences = sMsg;
  /**
   * "MsgSubChapters" = "Text" <p></p>
   * Der Nachrichten Prototyp im Falle von zu vielen oder zu wenigen direkten Unterkapiteln
   * <p></p> default: "%1$s %2$s %3$d %4$s (%5$d)"
   */
  @Expose
  @SerializedName("MsgSubChapters")
  private String cMsgSubChapters = sMsg;
  /**
   * "FromLevel" = int Zahl >= -19 <p></p>
   * Startlevel für die Suche in der Dokumenten Hierarchie
   * <p></p> default: -19
   */
  @Expose
  @SerializedName("FromLevel")
  private int cFromLevel = -19;
  /**
   * "MaxChapterDepth" = int Zahl >= -1 <p></p>
   * Maximale Kapiteltiefe, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MaxChapterDepth")
  private int cMaxChapterDepth = -1;
  /**
   * "MaxSentences" = int Zahl >= -1 <p></p>
   * Maximale Anzahl an Sätzen pro Abschnitt (chapter, section, usw), -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MaxSentences")
  private int cMaxSentences = -1;
  /**
   * "MaxSubChapters" = int Zahl >= -1 <p></p>
   * Maximale Anzahl an direkten Unterkapiteln , -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MaxSubChapters")
  private int cMaxSubChapters = -1;
  /**
   * "MinSentences" = int Zahl >= -1 <p></p>
   * Minimale Anzahl an Sätzen pro Abschnitt (chapter, section, usw), -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MinSentences")
  private int cMinSentences = -1;
  /**
   * "MinSubChapters" = int Zahl >= -1 <p></p>
   * Minimale Anzahl an direkten Unterkapiteln , -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MinSubChapters")
  private int cMinSubChapters = -1;
  /**
   * "ToLevel" = int Zahl >= -19 <p></p>
   * Endlevel für die Suche in der Dokumenten Hierarchie
   * <p></p> default: Integer.MAX_VALUE
   */
  @Expose
  @SerializedName("ToLevel")
  private int cToLevel = Integer.MAX_VALUE;

  private void checkChapterDepth(DocumentTree tree, int length) {
    if (tree == null) {
      mLog.fine("tree is null in checkSubChapters");
    } else {
      if (tree.getLevel() > -20) {
        if (length < 1) {
          mLog.info(new Result(mName, tree.getPosition(), String.format(cMsgChapters, tree.getHeadline(), "is deeper as", cMaxChapterDepth, "chapters", (cMaxChapterDepth + Math.abs(length) + 1))).toString());
        }
      }
      for (DocumentTree child : tree.child()) {
        checkChapterDepth(child, length - 1);
      }
    }
  }

  private void checkSentences(DocumentTree tree) {
    if (tree == null) {
      mLog.fine("tree is null in checkSubChapters");
    } else {
      if ((tree.getLevel() > -20) && (tree.getLevel() >= cFromLevel) && (tree.getLevel() <= cToLevel)) {
        if ((cMinSentences > 0) && (tree.getTexts().size() < cMinSentences)) {
          mLog.info(new Result(mName, tree.getPosition(), String.format(cMsgSentences, tree.getHeadline(), "has less than", cMinSentences, "sentences", tree.getTexts().size())).toString());
        }
        if ((cMaxSentences > -1) && (tree.getTexts().size() > cMaxSentences)) {
          mLog.info(new Result(mName, tree.getPosition(), String.format(cMsgSentences, tree.getHeadline(), "has more than", cMaxSentences, "sentences", tree.getTexts().size())).toString());
        }
      }
      for (DocumentTree child : tree.child()) {
        checkSubChapters(child);
      }
    }
  }

  private void checkSubChapters(DocumentTree tree) {
    if (tree == null) {
      mLog.fine("tree is null in checkSubChapters");
    } else {
      if ((tree.getLevel() > -20) && (tree.getLevel() >= cFromLevel) && (tree.getLevel() <= cToLevel)) {
        if ((cMinSubChapters > 0) && !tree.child().isEmpty() && (tree.child().size() < cMinSubChapters)) {
          mLog.info(new Result(mName, tree.getPosition(), String.format(cMsgSubChapters, tree.getHeadline(), "has less than", cMinSubChapters, "subchapters", tree.child().size())).toString());
        }
        if ((cMaxSubChapters > -1) && (tree.child().size() > cMaxSubChapters)) {
          mLog.info(new Result(mName, tree.getPosition(), String.format(cMsgSubChapters, tree.getHeadline(), "has more than", cMaxSubChapters, "subchapters", tree.child().size())).toString());
        }
      }
      for (DocumentTree child : tree.child()) {
        checkSubChapters(child);
      }
    }
  }

  @Override
  protected void validation() {
    try {
      String.format(cMsgSubChapters, "test", "test", 10, "test", 10);
    } catch (IllegalFormatException e) {
      mLog.throwing(ChapterCheck.class.getName(), mName, e);
      cMsgSubChapters = sMsg;
    }
    try {
      String.format(cMsgChapters, "test", "test", 10, "test", 10);
    } catch (IllegalFormatException e) {
      mLog.throwing(ChapterCheck.class.getName(), mName, e);
      cMsgChapters = sMsg;
    }
    try {
      String.format(cMsgSentences, "test", "test", 10, "test", 10);
    } catch (IllegalFormatException e) {
      mLog.throwing(ChapterCheck.class.getName(), mName, e);
      cMsgSentences = sMsg;
    }
  }

  @Override
  public void runModule() {
    if (!((cMinSubChapters <= -1) && (cMaxSubChapters <= -1))) {
      checkSubChapters(Api.getDocumentTreeRoot());
    }
    if (cMaxChapterDepth > -1) {
      checkChapterDepth(Api.getDocumentTreeRoot(), cMaxChapterDepth);
    }
    if (!((cMinSentences <= -1) && (cMaxSentences <= -1))) {
      checkSentences(Api.getDocumentTreeRoot());
    }

  }
}
