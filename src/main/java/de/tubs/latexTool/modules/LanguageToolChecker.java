package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.Command;
import de.tubs.latexTool.core.entrys.Environment;
import de.tubs.latexTool.core.entrys.Result;
import de.tubs.latexTool.core.entrys.Text;
import de.tubs.latexTool.core.util.Misc;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;
import org.languagetool.rules.Rule;
import org.languagetool.rules.RuleMatch;
import org.languagetool.rules.patterns.PatternRule;

import java.io.IOException;
import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dieses Modul ist ein Wrapper für das LanguageTool <p></p>
 * Mögliche Belegungen für das "Source" Attribut sind: "text", "environment", "command", "headline" oder "bullet"
 */
public class LanguageToolChecker extends Module {

  private static final String sMsg = "Potential typo: %1$s %nText: %2$s %nSuggested correction(s):%3$s";
  /**
   * "Msg" = "Text" <p></p>
   * Der Nachrichten Prototyp
   * <p></p> default: "Potential typo: %1$s %nText: %2$s %nSuggested correction(s):%3$s"
   */
  @Expose
  @SerializedName("Msg")
  private String cMsg = sMsg;
  /**
   * Pattern zum Suchen von Mathmode
   */
  private static final Pattern sPatternMathMode = Pattern.compile("(?<!\\\\)\\$(?<Content>.*?)(?<!\\\\)\\$", Pattern.DOTALL);
  /**
   * "OnlySpelling" = true / false <p></p>
   * Gibt an, ob nur Rechtschreibung geprüft werden soll
   * <p></p> default: false
   */
  @Expose
  @SerializedName("OnlySpelling")
  private boolean cOnlySpelling = false;
  /**
   * "RulesXML" = ["path"] <p></p>
   * Liste von Pfaden zu XML Dateien mit Regeln für das Languagetool, (die Pfade sind momentan noch abhängig vom
   * Aufrufpfad)
   * Deswegen am besten feste Pfade nutzen!
   * <p></p> default: empty
   */
  @Expose
  @SerializedName("RulesXML")
  private List<String> cRulesXML = new LinkedList<>();
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
  protected void validation() {
    try {
      String.format(cMsg, "test", "test", "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(LanguageToolChecker.class.getName(), mName, e);
      cMsg = sMsg;
    }
  }

  @Override
  public void runModule() {
    Language language;
    try {
      language = Language.getLanguageForShortName(Api.settings().getLanguage());
    } catch (IllegalArgumentException e) {
      mLog.throwing(LanguageToolChecker.class.getName(), "run", e);
      mLog.severe(String.format("Spelling, language %s is not supported", Api.settings().getLanguage()));
      return;
    }

    JLanguageTool jLanguageTool;
    try {
      jLanguageTool = new JLanguageTool(language);
    } catch (IOException e) {
      mLog.throwing(LanguageToolChecker.class.getName(), "run", e);
      mLog.severe(String.format("Spelling, can not load resources for the language %s", Api.settings().getLanguage()));
      return;
    }

    if (cOnlySpelling) {
      for (Rule rule : jLanguageTool.getAllRules()) {
        if (!rule.isDictionaryBasedSpellingRule()) {
          jLanguageTool.disableRule(rule.getId());
        }
      }
    }

    if (!cRulesXML.isEmpty()) {
      loadRules(jLanguageTool);
    }

    try {
      switch (cSource) {
        case "text":
          text(jLanguageTool);
          break;
        case "environment":
          environment(jLanguageTool);
          break;
        case "command":
          command(jLanguageTool);
          break;
        case "headline":
          headline(jLanguageTool);
          break;
        case "bullet":
          bullets(jLanguageTool);
          break;
        default:
          mLog.warning("only \"text\", \"environment\", \"command\", \"headline\" or \"bullet\" are allowed as an argument to Source.");
      }
    } catch (IOException e) {
      mLog.throwing(LanguageToolChecker.class.getName(), "run", e);
      mLog.severe(String.format("Spelling, can not connect to the native hunspell binaries"));
    }
  }

  // ToDo path besser benutzen
  private void loadRules(JLanguageTool jLanguageTool) {
    for (String path : cRulesXML) {
      try {
        List<PatternRule> rules = jLanguageTool.loadPatternRules(path);
        for (PatternRule rule : rules) {
          jLanguageTool.addRule(rule);
        }
      } catch (IOException e) {
        mLog.throwing(LanguageToolChecker.class.getName(), "loadRules", e);
      }
    }
  }

  private void text(JLanguageTool jLanguageTool) throws IOException {
    mLog.fine("start text spellchecking");
    List<RuleMatch> matches;
    List<Text> texts = Api.allTexts();
    for (Text text : texts) {
      matches = jLanguageTool.check(text.getText());
      for (RuleMatch ruleMatch : matches) {
        if (isNotInMathMode(text.getText(), ruleMatch)) {
          mLog.info(new Result(mName, text.getPosition(),
                  String.format(cMsg, ruleMatch.getMessage(), text.getText(),
                          ruleMatch.getSuggestedReplacements())
          ).toString());
        }
      }
    }
    mLog.fine("finish text spellchecking");
  }

  private void environment(JLanguageTool jLanguageTool) throws IOException {
    mLog.fine("start environment spellchecking");
    List<RuleMatch> matches;
    List<Environment> environments = Api.getEnvironments(Misc.iterableToString(cSourceList, !cUseRegex, !cUseRegex));
    for (Environment environment : environments) {
      String content = environment.getContent();
      content = Misc.maskingLatex(content, true);
      content = Misc.maskingEnvironment(content);
      content = Misc.removeLatex(content);
      content = content.replaceAll("\\s+", " ");
      matches = jLanguageTool.check(content);
      for (RuleMatch ruleMatch : matches) {
        if (isNotInMathMode(environment.getContent(), ruleMatch)) {
          mLog.info(new Result(mName, environment.getPosition(),
                  String.format(cMsg, ruleMatch.getMessage(), content,
                          ruleMatch.getSuggestedReplacements())
          ).toString());
        }
      }
    }
    mLog.fine("finish environment spellchecking");
  }

  private void command(JLanguageTool jLanguageTool) throws IOException {
    mLog.fine("start command spellchecking");
    List<RuleMatch> matches;
    List<Command> commands = Api.getCommands(Misc.iterableToString(cSourceList, !cUseRegex, !cUseRegex));
    for (Command command : commands) {
      for (String arg : command.getArgs()) {
        matches = jLanguageTool.check(arg);
        for (RuleMatch ruleMatch : matches) {
          if (isNotInMathMode(arg, ruleMatch)) {
            mLog.info(new Result(mName, command.getPosition(),
                    String.format(cMsg, ruleMatch.getMessage(), arg,
                            ruleMatch.getSuggestedReplacements())
            ).toString());
          }
        }
      }
    }
    mLog.fine("finish command spellchecking");
  }

  private void headline(JLanguageTool jLanguageTool) throws IOException {
    mLog.fine("start headline spellchecking");
    List<RuleMatch> matches;
    List<Text> headlines = Api.allHeadlines();
    for (Text headline : headlines) {
      matches = jLanguageTool.check(headline.getText());
      for (RuleMatch ruleMatch : matches) {
        if (isNotInMathMode(headline.getText(), ruleMatch)) {
          mLog.info(new Result(mName, headline.getPosition(),
                  String.format(cMsg, ruleMatch.getMessage(), headline.getText(),
                          ruleMatch.getSuggestedReplacements())
          ).toString());
        }
      }
    }
    mLog.fine("finish headline spellchecking");
  }

  private void bullets(JLanguageTool jLanguageTool) throws IOException {
    mLog.fine("start bullets spellchecking");
    List<RuleMatch> matches;
    List<Environment> environments = Api.getEnvironments(Misc.iterableToString(cSourceList, !cUseRegex, !cUseRegex));
    for (Environment environment : environments) {
      List<Text> texts = environment.getItems();
      for (Text text : texts) {
        String cText = text.getMasked();
        matches = jLanguageTool.check(cText);
        for (RuleMatch ruleMatch : matches) {
          if (isNotInMathMode(cText, ruleMatch)) {
            mLog.info(new Result(mName, text.getPosition(),
                    String.format(cMsg, ruleMatch.getMessage(), cText,
                            ruleMatch.getSuggestedReplacements())
            ).toString());
          }
        }
      }

    }
    mLog.fine("finish bullets spellchecking");
  }

  /**
   * Testet ob ein Fehler im Math Mode begangen wurde
   *
   * @param text
   * @param match
   * @return
   */
  private boolean isNotInMathMode(String text, RuleMatch match) {
    Matcher m = sPatternMathMode.matcher(text);
    while (m.find()) {
      if ((m.start() <= match.getFromPos()) && (m.end() >= match.getToPos())) {
        return false;
      }
    }
    return true;
  }
}
