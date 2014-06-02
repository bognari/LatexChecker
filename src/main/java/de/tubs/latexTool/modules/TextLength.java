package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.*;
import de.tubs.latexTool.core.util.Misc;

import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;

/**
 * Dies ist ein Modul, mit dem Sätze auf ihre Länge getestet werden <p></p>
 * Mögliche Belegungen für das "Source" Attribut sind: "text", "environment", "command", "headline" oder "bullet"
 */
public class TextLength extends Module {
  private static final String sMsg = "\"%1$s\" %n %2$s %3$d %4$s";
  /**
   * "Msg" = "Text" <p></p>
   * Der Nachrichten Prototyp
   * <p></p> default: "\"%1$s\" %n %2$s %3$d %4$s"
   */
  @Expose
  @SerializedName("Msg")
  private String cMsg = sMsg;
  /**
   * "HeadlineFrom" = int Zahl >= -19 <p></p>
   * Startlevel für die Suche in der Dokumenten Hierarchie
   * <p></p> default: -19
   */
  @Expose
  @SerializedName("HeadlineFrom")
  private int cHeadlineFrom = -19;
  /**
   * "HeadlineTo" = int Zahl >= -19 <p></p>
   * Endlevel für die Suche in der Dokumenten Hierarchie
   * <p></p> default: Integer.MAX_VALUE
   */
  @Expose
  @SerializedName("HeadlineTo")
  private int cHeadlineTo = Integer.MAX_VALUE;
  /**
   * "MaxChars" = int Zahl >= -1 <p></p>
   * Maximale Anzahl Zeichen pro Satz, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MaxChars")
  private int cMaxChars = -1;
  /**
   * "MaxWords" = int Zahl >= -1 <p></p>
   * Maximale Anzahl Wörtern pro Satz, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MaxWords")
  private int cMaxWords = -1;
  /**
   * "MinChars" = int Zahl >= -1 <p></p>
   * Minimale Anzahl Zeichen pro Satz, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MinChars")
  private int cMinChars = -1;
  /**
   * "MinWords" = int Zahl >= -1 <p></p>
   * Minimale Anzahl Wörtern pro Satz, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MinWords")
  private int cMinWords = -1;
  /**
   * "Source" = "Text" <p></p>
   * Die Eingabemöglichkeiten für "Source" sind in der Modul Beschreibung enthalten
   * <p></p> default: text
   */
  @Expose
  @SerializedName("Source")
  private String cSource = "text";
  /**
   * "SourceList" = ["Text"] <p></p>
   * Die Eingabemöglichkeiten für "SourceList" sind von "Source" abhängig (für Regex setzte UseRegex auf true)
   * <p></p> default: empty
   */
  @Expose
  @SerializedName("SourceList")
  private List<String> cSourceList = new LinkedList<>();
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
      String.format(cMsg, "test", "test", 10, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(TextLength.class.getName(), mName, e);
      cMsg = sMsg;
    }
  }

  @Override
  public void runModule() {
    switch (cSource) {
      case "text":
        text();
        break;
      case "environment":
        environment();
        break;
      case "command":
        command();
        break;
      case "headline":
        headline();
        break;
      case "bullet":
        bullet();
        break;
      default:
        mLog.warning("only \"text\", \"environment\", \"command\", \"headline\" or \"bullet\" are allowed as an argument to Source.");
    }
  }

  private void text() {
    List<Text> texts = Api.allTexts();
    for (Text text : texts) {
      check(text.getText(), text.getPosition());
    }
  }

  private void environment() {
    if (!cSourceList.isEmpty()) {
      List<Environment> environments = Api.getEnvironments(Misc.iterableToString(cSourceList, !cUseRegex, !cUseRegex));
      for (Environment environment : environments) {
        check(environment.getContent(), environment.getPosition());
      }
    }
  }

  private void command() {
    if (!cSourceList.isEmpty()) {
      List<Command> commands = Api.getCommands(Misc.iterableToString(cSourceList, !cUseRegex, !cUseRegex));
      for (Command command : commands) {
        for (String arg : command.getArgs()) {
          check(arg, command.getPosition());
        }
      }
    }
  }

  private void headline() {
    List<Text> texts = Api.allHeadlines();
    for (Text text : texts) {
      if ((text.getDocumentTree().getLevel() >= cHeadlineFrom) && (text.getDocumentTree().getLevel() <= cHeadlineTo)) {
        check(text.getText(), text.getPosition());
      }
    }
  }

  private void bullet() {
    if (!cSourceList.isEmpty()) {
      List<Environment> environments = Api.getEnvironments(Misc.iterableToString(cSourceList, !cUseRegex, !cUseRegex));
      for (Environment environment : environments) {
        for (Text text : environment.getItems()) {
          check(text.getMasked(), text.getPosition());
        }
      }
    }
  }

  private void check(String text, Position position) {
    if ((cMinChars > -1) && (text.length() < cMinChars)) {
      mLog.info(new Result(mName, position, String.format(cMsg, text, "has less than", cMinChars, "Chars")).toString());
    }
    if ((cMaxChars > -1) && (text.length() > cMaxChars)) {
      mLog.info(new Result(mName, position, String.format(cMsg, text, "has more than", cMaxChars, "Chars")).toString());
    }

    if ((cMinWords > -1) || (cMaxWords > -1)) {
      String[] words = text.split("\\s+");

      if ((cMinWords > -1) && (words.length < cMinWords)) {
        mLog.info(new Result(mName, position, String.format(cMsg, text, "has less than", cMinWords, "Words")).toString());
      }
      if ((cMaxWords > -1) && (words.length > cMaxWords)) {
        mLog.info(new Result(mName, position, String.format(cMsg, text, "has more than", cMaxWords, "Words")).toString());
      }
    }
  }
}
