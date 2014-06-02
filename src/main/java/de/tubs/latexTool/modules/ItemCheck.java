package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.Environment;
import de.tubs.latexTool.core.entrys.Result;
import de.tubs.latexTool.core.entrys.Text;
import de.tubs.latexTool.core.util.Misc;

import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;

/**
 * Mit dieser Klasse kann eine itemize-Umgebung auf die Anzahl ihrer
 * Bullets überprüft werden. Zudem lässt sich ein einzelnes item auf die Anzahl
 * seiner Sätze hin überprüfen.
 */

public class ItemCheck extends Module {
  private static final String sMsg = "%1$s %2$d %3$s";
  /**
   * "MsgItem" = "Text" <p></p>
   * Der Nachrichten Prototyp im Falle zu vieler oder zu weniger Items pro Umgebung (mit Verschachtelung)
   * <p></p> default: "%1$s %2$d %3$s"
   */
  @Expose
  @SerializedName("MsgItem")
  private String cMsgItem = sMsg;
  /**
   * "MsgSentence" = "Text" <p></p>
   * Der Nachrichten Prototyp im Falle zu vieler oder zu weniger Sätze pro Item
   * <p></p> default: "%1$s %2$d %3$s"
   */
  @Expose
  @SerializedName("MsgSentence")
  private String cMsgSentence = sMsg;
  /**
   * "Environments" = ["Umgebung"] <p></p>
   * Liste zu prüfenden Umgebungen (für Regex setzte UseRegex auf true)
   * <p></p> default: empty
   */
  @Expose
  @SerializedName("Environments")
  private List<String> cEnvironments = new LinkedList<>();
  /**
   * "MaxItems" = int Zahl >= -1 <p></p>
   * Maximale Anzahl an Items pro Umgebung (verschachtelte zählen dazu), -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MaxItems")
  private int cMaxItems = -1;
  /**
   * "MaxSentences" = int Zahl >= -1 <p></p>
   * Maximale Anzahl Sätzen pro Item, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MaxSentences")
  private int cMaxSentences = -1;
  /**
   * "MinItems" = int Zahl >= -1 <p></p>
   * Minimale Anzahl an Items pro Umgebung (verschachtelte zählen dazu), -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MinItems")
  private int cMinItems = -1;
  /**
   * "MinSentences" = int Zahl >= -1 <p></p>
   * Maximale Anzahl Sätzen pro Item, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MinSentences")
  private int cMinSentences = -1;
  /**
   * "UseRegex" = true / false <p></p>
   * Gibt an, ob Regex in Listen benutzt wird
   * <p></p> default: false
   */
  @Expose
  @SerializedName("UseRegex")
  private boolean cUseRegex = false;

  @Override
  public void validation() {
    try {
      String.format(cMsgSentence, "test", 10, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(ItemCheck.class.getName(), mName, e);
      cMsgSentence = sMsg;
    }
    try {
      String.format(cMsgItem, "test", 10, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(ItemCheck.class.getName(), mName, e);
      cMsgItem = sMsg;
    }
  }

  @Override
  public void runModule() {
    List<Environment> environments = Api.getEnvironments(Misc.iterableToString(cEnvironments, !cUseRegex, !cUseRegex));
    for (Environment environment : environments) {
      List<Text> items = environment.getItems();
      checkItems(environment, items);
      checkSentence(items);
    }
  }

  private void checkItems(Environment environment, List<Text> items) {
    if ((cMinItems > -1) && (items.size() < cMinItems)) {
      mLog.info(new Result(mName, environment.getPosition(), String.format(cMsgItem, "has less than", cMinItems, "items")).toString());
    }
    if ((cMaxItems > -1) && (items.size() > cMaxItems)) {
      mLog.info(new Result(mName, environment.getPosition(), String.format(cMsgItem, "has more than", cMaxItems, "items")).toString());
    }
  }

  private void checkSentence(List<Text> items) {
    for (Text item : items) {
      String cText = item.getMasked();
      List<String> sentences = Misc.getSentences(cText);

      if ((cMinSentences > -1) && (sentences.size() < cMinSentences)) {
        mLog.info(new Result(mName, item.getPosition(), String.format(cMsgSentence, "has less than", cMinSentences, "sentences")).toString());
      }
      if ((cMaxSentences > -1) && (sentences.size() > cMaxSentences)) {
        mLog.info(new Result(mName, item.getPosition(), String.format(cMsgSentence, "has more than", cMaxSentences, "sentences")).toString());
      }
    }
  }
}
