package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.Paragraph;
import de.tubs.latexTool.core.entrys.Result;

import java.util.IllegalFormatException;
import java.util.List;

/**
 * Dies ist ein Modul, mit dem Paragraphen auf ihre Länge getestet werden
 */
public class ParagraphLength extends Module {

  private static final String sMsg = "%1$s %2$d %3$s";
  /**
   * "Msg" = "Text" <p></p>
   * Der Nachrichten Prototyp
   * <p></p> default: "%1$s %2$d %3$s"
   */
  @Expose
  @SerializedName("Msg")
  private String cMsg = sMsg;
  /**
   * "MaxSentences" = int Zahl >= -1 <p></p>
   * Maximale Anzahl Sätzen pro Paragraph, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MaxSentences")
  private int cMaxSentences = -1;
  /**
   * "MinSentences" = int Zahl >= -1 <p></p>
   * Minimale Anzahl Sätzen pro Paragraph, -1 deaktiviert die Prüfung
   * <p></p> default: -1
   */
  @Expose
  @SerializedName("MinSentences")
  private int cMinSentences = -1;

  @Override
  public void validation() {
    try {
      String.format(cMsg, "test", 10, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(ParagraphLength.class.getName(), mName, e);
      cMsg = sMsg;
    }
  }

  @Override
  public void runModule() {
    List<Paragraph> paragraphs = Api.allParagraphs();

    for (Paragraph paragraph : paragraphs) {
      if ((cMinSentences > -1) && (paragraph.getTexts().size() < cMinSentences)) {
        mLog.info(new Result(mName, paragraph.getPosition(), String.format(cMsg, "has less than", cMinSentences, "sentences")).toString());
      }
      if ((cMaxSentences > -1) && (paragraph.getTexts().size() > cMaxSentences)) {
        mLog.info(new Result(mName, paragraph.getPosition(), String.format(cMsg, "has more than", cMaxSentences, "sentences")).toString());
      }
    }
  }
}
