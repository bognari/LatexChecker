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

/**
 * Dieses Modul prüft auf Abkürzungen mit verbotenen "Zwischenraum"
 * Die Abkürzungen werden aus dem globalem Abkürzungsverzeichnis geladen.
 */
public class AbbreviationsCheck extends Module {

  private static final String sMsg = "bad abbreviation \"%s\"";
  /**
   * "Msg" : "Text" <p></p>
   * Der Nachrichten Prototyp
   * <p></p> default: "bad abbreviation \"%s\""
   */
  @Expose
  @SerializedName("Msg")
  private String cMsg = sMsg;
  /**
   * "BadSeparators" = ["Text"] <p></p>
   * Eine Liste aller verbotenen Zwischenräume (für Regex setzte UseRegex auf true)
   * <p></p> default: empty
   */
  @Expose
  @SerializedName("BadSeparators")
  private List<String> cBadSeparators = new LinkedList<>();
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
      String.format(cMsg, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(ChapterCheck.class.getName(), mName, e);
      cMsg = sMsg;
    }
  }

  @Override
  public void runModule() {
    Set<String> abbreviations = Abbreviation.geAbbreviations();
    String badSeparators = Misc.iterableToString(cBadSeparators, !cUseRegex, !cUseRegex);
    String tex = Api.getRawContent();
    Pattern pattern;
    Matcher matcher;
    for (String abbreviation : abbreviations) {
      if (abbreviation.indexOf(' ') > -1) { // es gibt mindestens ein Leerzeichen
        pattern = Pattern.compile(abbreviation.replaceAll(" ", "(" + Matcher.quoteReplacement(badSeparators) + ")"), Pattern.CASE_INSENSITIVE);

        matcher = pattern.matcher(tex);

        while (matcher.find()) {
          mLog.info(new Result(mName, Api.getPosition(matcher.start()), String.format(cMsg, matcher.group())).toString());
        }
      }
    }
  }
}
