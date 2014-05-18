package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.Result;
import de.tubs.latexTool.core.util.Abbreviation;
import de.tubs.latexTool.core.util.Misc;

import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbbreviationsCheck extends Module {

  @Expose
  @SerializedName("BadSeparators")
  private List<String> mBadSeparators = new LinkedList<>();
  @Expose
  @SerializedName("Msg")
  private String mMsg = "bad abbreviation \"%s\"";

  /**
   * When an object implementing interface <code>Runnable</code> is used
   * to create a thread, starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may
   * take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    Set<String> abbreviations = Abbreviation.geAbbreviations();
    String badSeparators = Misc.iterableToString(mBadSeparators, true);
    String tex = Api.getRawContent();
    Pattern pattern;
    Matcher matcher;
    for (String abbreviation : abbreviations) {
      if (abbreviation.indexOf(' ') > -1) { // es gibt mindestens ein Leerzeichen
        pattern = Pattern.compile(abbreviation.replaceAll(" ", "(" + Matcher.quoteReplacement(badSeparators) + ")"), Pattern.CASE_INSENSITIVE);

        matcher = pattern.matcher(tex);

        while (matcher.find()) {
          mLog.info(new Result(mName, Api.getPosition(matcher.start()), String.format(mMsg, matcher.group())).toString());
        }
      }
    }
  }

  @Override
  protected void validation() {
    try {
      String.format(mMsg, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(ChapterCheck.class.getName(), mName, e);
      mMsg = "bad abbreviation \"%s\"";
    }
  }
}
