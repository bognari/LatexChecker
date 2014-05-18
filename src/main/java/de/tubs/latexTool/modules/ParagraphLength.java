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
 * Mögliche Optionen sind:
 * <p></p>
 * ############## <br></br>
 * MinSentences<br></br>
 * MaxSentences<br></br>
 * Msg<br></br>
 * ##############<br></br>
 */
public class ParagraphLength extends Module {
  @Expose
  @SerializedName("MaxSentences")
  private int mMaxSentences = -1;
  @Expose
  @SerializedName("MinSentences")
  private int mMinSentences = -1;
  @Expose
  @SerializedName("Msg")
  private String mMsg = "%1$s %2$d %3$s";

  @Override
  public void run() {
    mLog.fine(String.format("%s start", mName));

    List<Paragraph> paragraphs = Api.allParagraphs();

    for (Paragraph paragraph : paragraphs) {
      if (mMinSentences > -1 && paragraph.getTexts().size() < mMinSentences) {
        mLog.info(new Result(mName, paragraph.getPosition(), String.format(mMsg, "has less than", mMinSentences, "sentences")).toString());
      }
      if (mMaxSentences > -1 && paragraph.getTexts().size() > mMaxSentences) {
        mLog.info(new Result(mName, paragraph.getPosition(), String.format(mMsg, "has more than", mMaxSentences, "sentences")).toString());
      }
    }
    mLog.fine(String.format("%s finish", mName));
  }

  @Override
  public void validation() {
    try {
      String.format(mMsg, "test", 10, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(ParagraphLength.class.getName(), mName, e);
      mMsg = "%1$s %2$d %3$s";
    }
  }
}
