package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.Command;
import de.tubs.latexTool.core.entrys.Environment;
import de.tubs.latexTool.core.entrys.Result;
import de.tubs.latexTool.core.entrys.Text;
import de.tubs.latexTool.core.util.Misc;

import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Dies ist ein Modul, mit dem Sätze mit regulären Ausdrücken bearbeitet werden können.
 * Mögliche Optionen sind:
 * <p></p>
 * ############## <br></br>
 * WordList<br></br>
 * OnlyAtStart<br></br>
 * OnlyAtEnd<br></br>
 * ListStartWith<br></br>
 * ListEndWith<br></br>
 * UseEscaping<br></br>
 * CaseSensitive<br></br>
 * Msg<br></br>
 * Source<br></br>
 * SourceList<br></br>
 * HeadlineFrom<br></br>
 * HeadlineTo<br></br>
 * ##############<br></br>
 */
public class TextRegEx extends Module {
  @Expose
  @SerializedName("CaseSensitive")
  private boolean mCaseSensitive = true;
  @Expose
  @SerializedName("HeadlineFrom")
  private int mHeadlineFrom = -19;
  @Expose
  @SerializedName("HeadlineTo")
  private int mHeadlineTo = Integer.MAX_VALUE;
  @Expose
  @SerializedName("ListEndWith")
  private String mListEndWith = "";
  @Expose
  @SerializedName("ListStartWith")
  private String mListStartWith = "";
  @Expose
  @SerializedName("Msg")
  private String mMsg = "find: \"%1$s\", between %2$d and %3$d";
  @Expose
  @SerializedName("OnlyAtEnd")
  private boolean mOnlyAtEnd = false;
  @Expose
  @SerializedName("OnlyAtStart")
  private boolean mOnlyAtStart = false;
  @Expose
  @SerializedName("Source")
  private String mSource = "text";
  @Expose
  @SerializedName("SourceList")
  private List<String> mSourceList = new LinkedList<>();
  @Expose
  @SerializedName("UseEscaping")
  private boolean mUseEscaping = true;
  @Expose
  @SerializedName("WordList")
  private List<String> mWordList = new LinkedList<>();
  private Pattern pattern;

  @Override
  public void run() {
    mLog.fine(String.format("%s start", mName));
    buildPattern();

    switch (mSource) {
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
        mLog.warning("only \"text\", \"command\", \"headline\", \"bullet\"  or \"environment\" are allowed as an argument to Source.");
    }

    mLog.fine(String.format("%s finish", mName));
  }

  /**
   * Baut das Suchpattern zusammen
   */
  private void buildPattern() {
    StringBuilder stringBuilder = new StringBuilder();

    if (mOnlyAtStart) {
      stringBuilder.append('^');
    }
    if (!mListStartWith.isEmpty()) {
      stringBuilder.append(mUseEscaping ? "\\Q" + mListStartWith + "\\E" : mListStartWith);
    }

    if (!mWordList.isEmpty()) {
      stringBuilder.append('(');
      stringBuilder.append(Misc.iterableToString(mWordList, mUseEscaping));
            /*for (String word : mWordList) {
                stringBuilder.append("\\b");
                stringBuilder.append(mUseEscaping ? "\\Q" + word + "\\E" : word);
                stringBuilder.append("\\b");
                stringBuilder.append('|');
            }
            stringBuilder.deleteCharAt(stringBuilder.length() - 1);*/
      stringBuilder.append(')');
    }

    if (!mListEndWith.isEmpty()) {
      stringBuilder.append(mUseEscaping ? "\\Q" + mListEndWith + "\\E" : mListEndWith);
    }
    if (mOnlyAtEnd) {
      stringBuilder.append('$');
    }
    if (mCaseSensitive) {
      pattern = Pattern.compile(stringBuilder.toString());
    } else {
      pattern = Pattern.compile(stringBuilder.toString(), Pattern.CASE_INSENSITIVE);
    }
  }

  private void text() {
    List<Text> texts = Api.allTexts();
    Matcher matcher;
    for (Text text : texts) {
      matcher = pattern.matcher(text.getText());
      while (matcher.find()) {
        mLog.info(new Result(mName, text.getPosition(), String.format(mMsg, matcher.group(), matcher.start(), matcher.end())).toString());
      }
    }
  }

  private void environment() {
    if (!mSourceList.isEmpty()) {
      List<Environment> environments = Api.getEnvironments(Misc.iterableToString(mSourceList, true));
      Matcher matcher;

      for (Environment environment : environments) {
        matcher = pattern.matcher(environment.getContent());
        while (matcher.find()) {
          mLog.info(new Result(mName, environment.getPosition(), String.format(mMsg, matcher.group(), matcher.start(), matcher.end())).toString());
        }
      }
    }
  }

  private void command() {
    if (!mSourceList.isEmpty()) {
      List<Command> commands = Api.getCommands(Misc.iterableToString(mSourceList, true));
      Matcher matcher;
      for (Command command : commands) {
        for (String arg : command.getArgs()) {
          matcher = pattern.matcher(arg);
          while (matcher.find()) {
            mLog.info(new Result(mName, command.getPosition(), String.format(mMsg, matcher.group(), matcher.start(), matcher.end())).toString());
          }
        }
      }
    }
  }

  private void headline() {
    List<Text> texts = Api.allHeadlines();
    Matcher matcher;
    for (Text text : texts) {
      if (text.getDocumentTree().getLevel() >= mHeadlineFrom && text.getDocumentTree().getLevel() <= mHeadlineTo) {
        matcher = pattern.matcher(text.getText());
        while (matcher.find()) {
          mLog.info(new Result(mName, text.getPosition(), String.format(mMsg, matcher.group(), matcher.start(), matcher.end())).toString());
        }
      }
    }
  }

  private void bullet() {
    if (!mSourceList.isEmpty()) {
      List<Environment> environments = Api.getEnvironments(Misc.iterableToString(mSourceList, true));
      Matcher matcher;

      for (Environment environment : environments) {
        for (Text text : environment.getItems()) {
          String cText = text.getMasked();
          matcher = pattern.matcher(cText);
          while (matcher.find()) {
            mLog.info(new Result(mName, text.getPosition(), String.format(mMsg, matcher.group(), matcher.start(), matcher.end())).toString());
          }
        }
      }
    }
  }

  @Override
  public void validation() {
    try {
      String.format(mMsg, "test", 10, 10);
    } catch (IllegalFormatException e) {
      mLog.throwing(TextRegEx.class.getName(), mName, e);
      mMsg = "find: \"%1$s\", between %2$d and %3$d";
    }
  }
}
