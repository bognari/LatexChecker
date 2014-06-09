package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.ChapterTree;
import de.tubs.latexTool.core.entrys.Environment;
import de.tubs.latexTool.core.entrys.IPosition;
import de.tubs.latexTool.core.entrys.Result;
import de.tubs.latexTool.core.entrys.Text;
import de.tubs.latexTool.core.util.Misc;

import java.util.*;

/**
 * Dieses Modul prüft, ob vor, nach oder zwischen zwei angegebenen Umgebungen mindestens n Sätze existieren.
 */
public class EnvTextCheck extends Module {
  private static final String sMsg = "%1$s %2$d %3$s";
  /**
   * "Msg" = "Text" <p></p>
   * Der Nachrichten Prototyp
   * <p></p> default: "%1$s %2$d %3$s"
   */
  @Expose
  @SerializedName("Msg")
  private String cMsg = sMsg;
  /**
   * "Environments" = ["Umgebung"] <p></p>
   * Liste zu prüfenden Umgebungen (für Regex setzte UseRegex auf true)
   * <p></p> default: empty
   */
  @Expose
  @SerializedName("Environments")
  private List<String> cEnvironments = new LinkedList<>();
  /*@Expose
  @SerializedName("MaxAfterSentences")
  private int mMaxAfterSentences = -1;*/
  /**
   * "MinAfterSentences" = int Zahl >= -1 <p></p>
   * Minimale Anzahl an Sätzen nach einer Umgebung bis zum Ende des Abschnitts, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MinAfterSentences")
  private int cMinAfterSentences = -1;
  /*@Expose
  @SerializedName("MaxBeforeSentences")
  private int mMaxBeforeSentences = -1;*/
  /**
   * "MinBeforeSentences" = int Zahl >= -1 <p></p>
   * Minimale Anzahl an Sätzen vor einer Umgebung seit Anfang des Abschnitts, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MinBeforeSentences")
  private int cMinBeforeSentences = -1;
  /*@Expose
  @SerializedName("MaxBetweenSentences")
  private int mMaxBetweenSentences = -1;*/
  /**
   * "MinBetweenSentences" = int Zahl >= -1 <p></p>
   * Minimale Anzahl an Sätzen zwischen zwei Umgebungen, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MinBetweenSentences")
  private int cMinBetweenSentences = -1;
  /**
   * "UseRegex" = true / false <p></p>
   * Gibt an, ob Regex in Listen benutzt wird
   * <p></p> default: false
   */
  @Expose
  @SerializedName("UseRegex")
  private boolean cUseRegex = false;
  private Map<Integer, IPosition> mMap = new TreeMap<>();

  private boolean isIn(IPosition env) {
    for (int key : mMap.keySet()) {
      IPosition position = mMap.get(key);
      if ((position.getStart() <= env.getStart()) && (env.getEnd() <= position.getEnd())) {
        return true;
      }
    }
    return false;
  }

  private void readEnvs() {
    List<Environment> environmentList = Api.getEnvironments(Misc.iterableToString(cEnvironments, !cUseRegex, !cUseRegex));

    for (Environment environment : environmentList) {
      if (!isIn(environment)) {
        mMap.put(environment.getStart(), environment);
      }
    }
  }

  private void readSections() {
    ChapterTree root = Api.getChapterTreeRoot();
    sectionRunner(root);
  }

  private void readText() {
    List<Text> textList = Api.allTexts();
    for (Text text : textList) {
      mMap.put(text.getStart(), text);
    }
  }

  private void runTest() {
    int sinceSection = -1;
    int sinceEnv = -1;
    for (int key : mMap.keySet()) {
      IPosition position = mMap.get(key);
      if (position instanceof ChapterTree) {
        if (sinceEnv > -1) {
          if ((cMinAfterSentences > -1) && (sinceEnv < cMinAfterSentences)) {
            mLog.info(new Result(mName, position.getPosition(), String.format(cMsg, "has less than", cMinAfterSentences, "sentences after the last environment")).toString());
          }
          /*if (mMaxAfterSentences > -1 && sinceEnv > mMaxAfterSentences) {
            mLog.info(new Result(mName, position.getPosition(), String.format(cMsg, "has more than", mMaxAfterSentences, "sentences after the last environment")).toString());
          }*/
        }
        sinceSection = 0;
      }
      if (position instanceof Environment) {
        if (sinceSection > -1) {
          if ((cMinBeforeSentences > -1) && (sinceSection < cMinBeforeSentences)) {
            mLog.info(new Result(mName, position.getPosition(), String.format(cMsg, "has less than", cMinBeforeSentences, "sentences before the next environment")).toString());
          }

          /*if (mMaxBeforeSentences > -1 && sinceSection > mMaxBeforeSentences) {
            mLog.info(new Result(mName, position.getPosition(), String.format(cMsg, "has more than", mMaxBeforeSentences, "sentences before the next environment")).toString());
          }*/
        }
        sinceSection += cMinBeforeSentences;
        if (sinceEnv > -1) {
          if ((cMinBetweenSentences > -1) && (sinceEnv < cMinBetweenSentences)) {
            mLog.info(new Result(mName, position.getPosition(), String.format(cMsg, "has less than", cMinBetweenSentences, "sentences between the last environment")).toString());
          }
          /*if (mMaxBetweenSentences > -1 && sinceEnv > mMaxBetweenSentences) {
            mLog.info(new Result(mName, position.getPosition(), String.format(cMsg, "has more than", mMaxBetweenSentences, "sentences between the last environment")).toString());
          }*/
        }
        sinceEnv = 0;
      }
      if (position instanceof Text) {
        sinceEnv++;
        sinceSection++;
      }
    }
  }

  private void sectionRunner(ChapterTree node) {
    if (node == null) {
      mLog.fine("Node is null in sectionRunner");
    } else {
      mMap.put(node.getStart(), node);
      for (ChapterTree child : node.child()) {
        sectionRunner(child);
      }
    }
  }

  @Override
  protected void validation() {
    try {
      String.format(cMsg, "test", 10, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(ChapterCheck.class.getName(), mName, e);
      cMsg = sMsg;
    }
  }

  @Override
  public void runModule() {
    readEnvs();
    readSections();
    readText();
    runTest();
  }
}
