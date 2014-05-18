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

import java.io.IOException;
import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dieses Modul ist ein Wrapper für das LanguageTool <p></p>
 * #############<br></br>
 * OnlySpelling<br></br>
 * Msg<br></br>
 * Source<br></br>
 * SourceList<br></br>
 * #############<br></br>
 */
public class Spelling extends Module {

  /**
   * Pattern zum Suchen von Mathmode
   */
  private static final Pattern sPatternMathMode = Pattern.compile("(?<!\\\\)\\$(?<Content>.*?)(?<!\\\\)\\$", Pattern.DOTALL);
  @Expose
  @SerializedName("Msg")
  private String mMsg = "Potential typo: %1$s %nText: %2$s %nSuggested correction(s):%3$s";
  @Expose
  @SerializedName("OnlySpelling")
  private boolean mOnlySpelling = false;
  @Expose
  @SerializedName("Source")
  private String mSource = "text";
  @Expose
  @SerializedName("SourceList")
  private List<String> mSourceList = new LinkedList<>();

  @Override
  public void run() {
    mLog.fine(String.format("%s start", mName));
    Language language;
    try {
      language = Language.getLanguageForShortName(Api.settings().getLanguage());
    } catch (IllegalArgumentException e) {
      mLog.throwing(Spelling.class.getName(), "run", e);
      mLog.severe(String.format("Spelling, language %s is not supported", Api.settings().getLanguage()));
      return;
    }

    JLanguageTool jLanguageTool;
    try {
      jLanguageTool = new JLanguageTool(language);
    } catch (IOException e) {
      mLog.throwing(Spelling.class.getName(), "run", e);
      mLog.severe(String.format("Spelling, can not load resources for the language %s", Api.settings().getLanguage()));
      return;
    }

    if (mOnlySpelling) {
      for (Rule rule : jLanguageTool.getAllRules()) {
        if (!rule.isDictionaryBasedSpellingRule()) {
          jLanguageTool.disableRule(rule.getId());
        }
      }
    }

    try {
      switch (mSource) {
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
          mLog.warning("only \"text\", \"command\", \"headline\", \"bullet\" or \"environment\" are allowed as an argument to Source.");
      }
    } catch (IOException e) {
      mLog.throwing(Spelling.class.getName(), "run", e);
      mLog.severe(String.format("Spelling, can not connect to the native hunspell binaries"));
      return;
    }
    mLog.fine(String.format("%s finish", mName));
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
                  String.format(mMsg, ruleMatch.getMessage(), text.getText(),
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
    List<Environment> environments = Api.getEnvironments(Misc.iterableToString(mSourceList, true));
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
                  String.format(mMsg, ruleMatch.getMessage(), content,
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
    List<Command> commands = Api.getCommands(Misc.iterableToString(mSourceList, true));
    for (Command command : commands) {
      for (String arg : command.getArgs()) {
        matches = jLanguageTool.check(arg);
        for (RuleMatch ruleMatch : matches) {
          if (isNotInMathMode(arg, ruleMatch)) {
            mLog.info(new Result(mName, command.getPosition(),
                    String.format(mMsg, ruleMatch.getMessage(), arg,
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
                  String.format(mMsg, ruleMatch.getMessage(), headline.getText(),
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
    List<Environment> environments = Api.getEnvironments(Misc.iterableToString(mSourceList, true));
    for (Environment environment : environments) {
      List<Text> texts = environment.getItems();
      for (Text text : texts) {
        String cText = text.getMasked();
        matches = jLanguageTool.check(cText);
        for (RuleMatch ruleMatch : matches) {
          if (isNotInMathMode(cText, ruleMatch)) {
            mLog.info(new Result(mName, text.getPosition(),
                    String.format(mMsg, ruleMatch.getMessage(), cText,
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
      if (m.start() <= match.getFromPos() && m.end() >= match.getToPos()) {
        return false;
      }
    }
    return true;
  }

  @Override
  protected void validation() {
    try {
      String.format(mMsg, "test", "test", "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(Spelling.class.getName(), mName, e);
      mMsg = "Potential typo: %1$s %nText: %2$s %nSuggested correction(s):%3$s";
    }
  }
}
