package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.*;
import de.tubs.latexTool.core.util.Misc;

import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dies ist ein Modul, mit dem Sätze mit regulären Ausdrücken bearbeitet werden können. <p></p>
 * Mögliche Belegungen für das "Source" Attribut sind: "text", "environment", "command", "headline", "bullet", "tex"
 * oder "latexText"
 */
public class TextRegex extends Module {
  private static final String sMsg = "find: \"%1$s\", between %2$d and %3$d";
  /**
   * "Msg" = "Text" <p></p>
   * Der Nachrichten Prototyp
   * <p></p> default: "find: \"%1$s\", between %2$d and %3$d"
   */
  @Expose
  @SerializedName("Msg")
  private String cMsg = sMsg;
  /**
   * "CaseSensitive" = true / false <p></p>
   * Gibt an, ob nach Groß- und Kleinschreibung unterschieden werden soll
   * <p></p> default: true
   */
  @Expose
  @SerializedName("CaseSensitive")
  private boolean cCaseSensitive = true;
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
   * "ListEndWith" = "Text" <p></p>
   * Ein Text, der nach einem Wort aus der Wortlist gematcht wird
   * <p></p> default: ""
   */
  @Expose
  @SerializedName("ListEndWith")
  private String cListEndWith = "";
  /**
   * "ListStartWith" = "Text" <p></p>
   * Ein Text, der vor einem Wort aus der Wortlist gematcht wird
   * <p></p> default: ""
   */
  @Expose
  @SerializedName("ListStartWith")
  private String cListStartWith = "";
  /**
   * "OnlyAtEnd" = true / false <p></p>
   * Gibt an, ob nur am Ende ($) gematcht wird
   * <p></p> default: false
   */
  @Expose
  @SerializedName("OnlyAtEnd")
  private boolean cOnlyAtEnd = false;
  /**
   * "OnlyAtStart" = true / false <p></p>
   * Gibt an, ob nur am Anfang (^) gematcht wird
   * <p></p> default: false
   */
  @Expose
  @SerializedName("OnlyAtStart")
  private boolean cOnlyAtStart = false;
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
   * "UseEscaping" = true / false <p></p>
   * Gibt an, alle eingaben escapt werden mit \\Q Text \\E
   * <p></p> default: true
   */
  @Expose
  @SerializedName("UseEscaping")
  private boolean cUseEscaping = true;
  /**
   * "UseRegex" = true / false <p></p>
   * Gibt an, ob Regex in Listen benutzt wird
   * <p></p> default: false
   */
  @Expose
  @SerializedName("UseRegex")
  private boolean cUseRegex = false;
  /**
   * "WordList" = ["Text"] <p></p>
   * Eine Liste von Wörtern, von denen eins gemacht wird
   * <p></p> default: empty
   */
  @Expose
  @SerializedName("WordList")
  private List<String> cWordList = new LinkedList<>();
  private Pattern pattern;

  @Override
  public void validation() {
    try {
      String.format(cMsg, "test", 10, 10);
    } catch (IllegalFormatException e) {
      mLog.throwing(TextRegex.class.getName(), mName, e);
      cMsg = sMsg;
    }
  }

  @Override
  public void runModule() {
    buildPattern();
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
      case "tex":
        tex();
        break;
      case "latexText":
        latexText();
        break;
      default:
        mLog.warning("only \"text\", \"environment\", \"command\", \"headline\", \"bullet\", \"tex\" or \"latexText\" are allowed as an argument to Source.");
    }
  }

  /**
   * Baut das Suchpattern zusammen
   */
  private void buildPattern() {
    StringBuilder stringBuilder = new StringBuilder();

    if (cOnlyAtStart) {
      stringBuilder.append('^');
    }
    if (!cListStartWith.isEmpty()) {
      stringBuilder.append(cUseEscaping ? ("\\Q" + cListStartWith + "\\E") : cListStartWith);
    }

    if (!cWordList.isEmpty()) {
      stringBuilder.append(Misc.iterableToString(cWordList, cUseEscaping, true));
    }

    if (!cListEndWith.isEmpty()) {
      stringBuilder.append(cUseEscaping ? ("\\Q" + cListEndWith + "\\E") : cListEndWith);
    }
    if (cOnlyAtEnd) {
      stringBuilder.append('$');
    }
    if (cCaseSensitive) {
      pattern = Pattern.compile(stringBuilder.toString());
    } else {
      pattern = Pattern.compile(stringBuilder.toString(), Pattern.CASE_INSENSITIVE);
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
    List<Headline> headlines = Api.allHeadlines();
    for (Headline headline : headlines) {
      if ((headline.getChapterTree().getLevel() >= cHeadlineFrom) && (headline.getChapterTree().getLevel() <= cHeadlineTo)) {
        check(headline.getHeadline(), headline.getPosition());
        if (headline.getShortHeadline() != null) {
          check(headline.getShortHeadline(), headline.getPosition());
        }
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

  private void tex() {
    Matcher matcher;
    String content = Api.getRawContent();
    matcher = pattern.matcher(content);
    while (matcher.find()) {
      mLog.info(new Result(mName, Api.getPosition(matcher.start()), String.format(cMsg, matcher.group(), matcher.start(), matcher.end())).toString());
    }
  }

  private void latexText() {
    List<Text> texts = Api.allLatexTexts();
    for (Text text : texts) {
      check(text.getText(), text.getPosition());
    }
  }

  private void check(String text, Position position) {
    Matcher matcher = pattern.matcher(text);
    while (matcher.find()) {
      mLog.info(new Result(mName, position, String.format(cMsg, matcher.group(), matcher.start(), matcher.end())).toString());
    }
  }
}
