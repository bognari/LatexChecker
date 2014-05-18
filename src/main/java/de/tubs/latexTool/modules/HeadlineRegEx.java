package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.Environment;
import de.tubs.latexTool.core.entrys.Result;
import de.tubs.latexTool.core.entrys.Text;
import de.tubs.latexTool.core.util.Misc;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jochen on 27.03.14.
 * Methode zur Überprüfung des Punktes 33: Paragraphentitel enden immer mit Punkt oder Doppelpunkt
 * Methode zur Überprüfung des Punktes 17: Keine Doppelpunkte in Überschriften und Grafik-/Tabellenunterschriften
 * Methode zur Überprüfung des Punktes 16: Mindestens n Wörter in Überschriften und Grafik-/Tabellenunterschriften.
 */
public class HeadlineRegEx extends Module {

  private static final Pattern sPatterCaption = Pattern.compile("(?<!\\\\)\\\\caption\\{(.*?)\\}");
  private static final Pattern sPatterHeadline = Pattern.compile("(:\\s*)$");
  private static final Pattern sPatterPara = Pattern.compile("((\\.|:)\\s*)$");
  @Expose
  @SerializedName("EnvList")
  private List<String> mEnvList = new LinkedList<>();
  @Expose
  @SerializedName("MaxWords")
  private int mMaxWords = -1;
  @Expose
  @SerializedName("MaxWordsCaption")
  private int mMaxWordsCaption = -1;
  @Expose
  @SerializedName("MinWords")
  private int mMinWords = -1;
  @Expose
  @SerializedName("MinWordsCaption")
  private int mMinWordsCaption = -1;

  public void run() {
    testParaHeadlines();
    CaptionColon();
  }

  private void testParaHeadlines() {
    List<Text> headlines = Api.allHeadlines();

    for (Text headline : headlines) {
      if (headline.getDocumentTree().getLevel() >= 40) {
        endsPoint(headline);
        headlineLength(headline);
      } else if (headline.getDocumentTree().getLevel() >= 0) {
        endsNoColon(headline);
        headlineLength(headline);
      }
    }
  }

  private void CaptionColon() {
    for (Environment env : Api.getEnvironments(Misc.iterableToString(mEnvList, true))) {

      Matcher matcher;
      matcher = sPatterCaption.matcher(env.getContent());
      if (matcher.find()) {
        String Caption = matcher.group(1);

        if (Caption.matches("^.*?:\\s*$")) {
          mLog.info(new Result(mName, env.getPosition(), String.format("Grafic/Table caption ends with colon")).toString());
        }
        CaptionLength(Caption, env);
      }
    }
  }

  private void endsPoint(Text headline) {
    Matcher matcher;
    matcher = sPatterPara.matcher(headline.getText());
    if (matcher.find()) {
      mLog.info(new Result(headline.getText(), headline.getPosition(), String.format("Paragraph title does not end with full stop or colon")).toString());
    }
  }

  private void headlineLength(Text headline) {
    if (mMaxWords > -1 && headline.getText().split(" ").length > mMaxWords) {
      mLog.info(new Result(headline.getText(), headline.getPosition(), String.format("Caption is longer than %d words", mMaxWords)).toString());
    }
    if (mMinWords > -1 && headline.getText().split(" ").length < mMinWords) {
      mLog.info(new Result(headline.getText(), headline.getPosition(), String.format("Caption is shorter than %d words", mMaxWords)).toString());
    }
  }

  private void endsNoColon(Text headline) {
    Matcher matcher;
    matcher = sPatterHeadline.matcher(headline.getText());
    if (matcher.find()) {
      mLog.info(new Result(headline.getText(), headline.getPosition(), String.format("Caption ends with colon")).toString());
    }
  }

  private void CaptionLength(String Caption, Environment env) {
    if (mMaxWordsCaption > -1 && Caption.split(" ").length > mMaxWordsCaption) {
      mLog.info(new Result(Caption, env.getPosition(), String.format("Caption is longer than %d words", mMaxWords)).toString());
    }
    if (mMinWordsCaption > -1 && Caption.split(" ").length < mMinWordsCaption) {
      mLog.info(new Result(Caption, env.getPosition(), String.format("Caption is shorter than %d words", mMaxWords)).toString());
    }
  }
}
