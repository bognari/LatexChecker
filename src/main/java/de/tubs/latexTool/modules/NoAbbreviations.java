package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.*;
import de.tubs.latexTool.core.util.Abbreviation;
import de.tubs.latexTool.core.util.Misc;

import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dies ist ein Modul, mit dem Sätze mit regulären Ausdrücken bearbeitet werden können. <p></p>
 * Mögliche Belegungen für das "Source" Attribut sind: "text", "environment", "command", "headline", "bullet", "tex"
 * oder "latexText"
 */
public class NoAbbreviations extends Module {
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
   * "Separators" = ["Text"] <p></p>
   * Eine Liste aller möglichen Zwischenräume (für Regex setzte UseRegex auf true)
   * <p></p> default: empty
   */
  @Expose
  @SerializedName("Separators")
  private List<String> cSeparators = new LinkedList<>();
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
  private Pattern pattern;

  @Override
  public void validation() {
    try {
      String.format(cMsg, "test", 10, 10);
    } catch (IllegalFormatException e) {
      mLog.throwing(NoAbbreviations.class.getName(), mName, e);
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
    String separators = Misc.iterableToString(cSeparators, !cUseRegex, false);
    Set<String> abbreviations = Abbreviation.geAbbreviations();
    List<String> abbs = new LinkedList<>();

    for (String abbreviation : abbreviations) {
      if (abbreviation.indexOf(' ') > -1) { // es gibt mindestens ein Leerzeichen
        abbs.add(abbreviation.replaceAll(" ", "(" + Matcher.quoteReplacement(separators) + ")"));
      } else {
        abbs.add(abbreviation);
      }
    }

    pattern = Pattern.compile(Misc.iterableToString(abbs, false, false));
  }

  private void text() {
    List<Text> texts = Api.allTexts();
    Matcher matcher;
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
