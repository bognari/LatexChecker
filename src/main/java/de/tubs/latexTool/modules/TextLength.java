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
 * Dies ist ein Modul, mit dem Sätze auf ihre Länge getestet werden
 * Mögliche Optionen sind:
 * <p></p>
 * ############## <br></br>
 * MinChars<br></br>
 * MaxChars<br></br>
 * MinWords<br></br>
 * MaxWords<br></br>
 * Msg<br></br>
 * Source<br></br>
 * SourceList<br></br>
 * HeadlineFrom<br></br>
 * HeadlineTo<br></br>
 * ##############<br></br>
 */
public class TextLength extends Module {
  @Expose
  @SerializedName("HeadlineFrom")
  private int mHeadlineFrom = -19;
  @Expose
  @SerializedName("HeadlineTo")
  private int mHeadlineTo = Integer.MAX_VALUE;
  @Expose
  @SerializedName("MaxChars")
  private int mMaxChars = -1;
  @Expose
  @SerializedName("MaxWords")
  private int mMaxWords = -1;
  @Expose
  @SerializedName("MinChars")
  private int mMinChars = -1;
  @Expose
  @SerializedName("MinWords")
  private int mMinWords = -1;
  @Expose
  @SerializedName("Msg")
  private String mMsg = "\"%1$s\" %n %2$s %3$d %4$s";
  @Expose
  @SerializedName("Source")
  private String mSource = "text";
  @Expose
  @SerializedName("SourceList")
  private List<String> mSourceList = new LinkedList<>();

  @Override
  public void run() {
    mLog.fine(String.format("%s start", mName));

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

  private void text() {
    List<Text> texts = Api.allTexts();
    for (Text text : texts) {
      check(text.getText(), text.getPosition());
    }
  }

  private void environment() {
    if (!mSourceList.isEmpty()) {
      List<Environment> environments = Api.getEnvironments(Misc.iterableToString(mSourceList, true));
      for (Environment environment : environments) {
        check(environment.getContent(), environment.getPosition());
      }
    }
  }

  private void command() {
    if (!mSourceList.isEmpty()) {
      List<Command> commands = Api.getCommands(Misc.iterableToString(mSourceList, true));
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
      if (text.getDocumentTree().getLevel() >= mHeadlineFrom && text.getDocumentTree().getLevel() <= mHeadlineTo) {
        check(text.getText(), text.getPosition());
      }
    }
  }

  private void bullet() {
    if (!mSourceList.isEmpty()) {
      List<Environment> environments = Api.getEnvironments(Misc.iterableToString(mSourceList, true));
      for (Environment environment : environments) {
        for (Text text : environment.getItems()) {
          check(text.getMasked(), text.getPosition());
        }
      }
    }
  }

  private void check(String text, Position position) {
    if (mMinChars > -1 && text.length() < mMinChars) {
      mLog.info(new Result(mName, position, String.format(mMsg, text, "has less than", mMinChars, "Chars")).toString());
    }
    if (mMaxChars > -1 && text.length() > mMaxChars) {
      mLog.info(new Result(mName, position, String.format(mMsg, text, "has more than", mMaxChars, "Chars")).toString());
    }

    if (mMinWords > -1 || mMaxWords > -1) {
      String[] words = text.split("\\s+");

      if (mMinWords > -1 && words.length < mMinWords) {
        mLog.info(new Result(mName, position, String.format(mMsg, text, "has less than", mMinWords, "Words")).toString());
      }
      if (mMaxWords > -1 && words.length > mMaxWords) {
        mLog.info(new Result(mName, position, String.format(mMsg, text, "has more than", mMaxWords, "Words")).toString());
      }
    }
  }

  @Override
  public void validation() {
    try {
      String.format(mMsg, "test", "test", 10, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(TextLength.class.getName(), mName, e);
      mMsg = "\"%1$s\" %n %2$s %3$d %4$s";
    }
  }
}
