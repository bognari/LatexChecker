package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.Environment;
import de.tubs.latexTool.core.entrys.Headline;
import de.tubs.latexTool.core.entrys.Result;
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

  /**
   * "Environments" = ["Umgebung"] <p></p>
   * Liste zu prüfenden Umgebungen (für Regex setzte UseRegex auf true)
   * <p></p> default: empty
   */
  @Expose
  @SerializedName("Environments")
  private List<String> cEnvironments = new LinkedList<>();
  /**
   * "MaxWords" = int Zahl >= -1 <p></p>
   * Maximale Anzahl an Wörtern in einer Überschrift, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MaxWords")
  private int cMaxWords = -1;
  /**
   * "MaxWordsCaption" = int Zahl >= -1 <p></p>
   * Maximale Anzahl an Wörtern in einer Beschreibung, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MaxWordsCaption")
  private int cMaxWordsCaption = -1;
  /**
   * "MinWords" = int Zahl >= -1 <p></p>
   * Minimale Anzahl an Wörtern in einer Überschrift, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MinWords")
  private int cMinWords = -1;
  /**
   * "MinWordsCaption" = int Zahl >= -1 <p></p>
   * Minimale Anzahl an Wörtern in einer Beschreibung, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MinWordsCaption")
  private int cMinWordsCaption = -1;
  /**
   * "UseRegex" = true / false <p></p>
   * Gibt an, ob Regex in Listen benutzt wird
   * <p></p> default: false
   */
  @Expose
  @SerializedName("UseRegex")
  private boolean cUseRegex = false;


  public void runModule() {
    testParaHeadlines();
    CaptionColon();
  }

  private void testParaHeadlines() {
    List<Headline> headlines = Api.allHeadlines();

    for (Headline headline : headlines) {
      if (headline.getChapterTree().getLevel() >= 40) {
        endsPoint(headline);
        headlineLength(headline);
      } else if (headline.getChapterTree().getLevel() >= 0) {
        endsNoColon(headline);
        headlineLength(headline);
      }
    }
  }

  private void CaptionColon() {
    for (Environment env : Api.getEnvironments(Misc.iterableToString(cEnvironments, !cUseRegex, !cUseRegex))) {

      Matcher matcher;
      matcher = sPatterCaption.matcher(env.getContent());
      if (matcher.find()) {
        String Caption = matcher.group(1);

        if (Caption.matches("^.*?:\\s*$")) {
          mLog.info(new Result(mName, env.getPosition(), String.format("Graphic/Table caption ends with colon")).toString());
        }
        CaptionLength(Caption, env);
      }
    }
  }

  private void endsPoint(Headline headline) {
    Matcher matcher;
    matcher = sPatterPara.matcher(headline.getHeadline());
    if (matcher.find()) {
      mLog.info(new Result(headline.getHeadline(), headline.getPosition(), String.format("Paragraph title does not end with full stop or colon")).toString());
    }
  }

  private void headlineLength(Headline headline) {
    String text = headline.getShortHeadline() != null ? headline.getShortHeadline() : headline.getHeadline();

    if ((cMaxWords > -1) && (text.split(" ").length > cMaxWords)) {
      mLog.info(new Result(text, headline.getPosition(), String.format("Caption is longer than %d words", cMaxWords)).toString());
    }
    if ((cMinWords > -1) && (text.split(" ").length < cMinWords)) {
      mLog.info(new Result(text, headline.getPosition(), String.format("Caption is shorter than %d words", cMaxWords)).toString());
    }
  }

  private void endsNoColon(Headline headline) {
    Matcher matcher;
    matcher = sPatterHeadline.matcher(headline.getHeadline());
    if (matcher.find()) {
      mLog.info(new Result(headline.getHeadline(), headline.getPosition(), String.format("Caption ends with colon")).toString());
    }
  }

  private void CaptionLength(String Caption, Environment env) {
    if ((cMaxWordsCaption > -1) && (Caption.split(" ").length > cMaxWordsCaption)) {
      mLog.info(new Result(Caption, env.getPosition(), String.format("Caption is longer than %d words", cMaxWords)).toString());
    }
    if ((cMinWordsCaption > -1) && (Caption.split(" ").length < cMinWordsCaption)) {
      mLog.info(new Result(Caption, env.getPosition(), String.format("Caption is shorter than %d words", cMaxWords)).toString());
    }
  }
}
