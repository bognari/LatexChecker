package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.Environment;
import de.tubs.latexTool.core.entrys.Result;
import de.tubs.latexTool.core.entrys.Text;
import de.tubs.latexTool.core.util.Misc;

import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Tessa
 *         <p></p>
 *         Mit dieser Klasse kann eine itemize-Umgebung auf die Anzahl ihrer
 *         Bullets überprüft werden. Zudem lässt sich ein einzelnes item auf die Anzahl
 *         seiner Sätze hin überprüfen.
 *         <p></p>
 *         ############## <br></br>
 *         MinSentences<br></br>
 *         MaxSentences<br></br>
 *         MinItems<br></br>
 *         MaxItems<br></br>
 *         MsgSentences<br></br>
 *         MsgItems<br></br>
 *         SourceList<br></br>
 *         ##############<br></br>
 */

public class ItemCheck extends Module {
  @Expose
  @SerializedName("MaxItems")
  private int mMaxItems = -1;
  @Expose
  @SerializedName("MaxSentences")
  private int mMaxSentences = -1;
  @Expose
  @SerializedName("MinItems")
  private int mMinItems = -1;
  @Expose
  @SerializedName("MinSentences")
  private int mMinSentences = -1;
  @Expose
  @SerializedName("MsgItem")
  private String mMsgItem = "%1$s %2$d %3$s";
  @Expose
  @SerializedName("MsgSentence")
  private String mMsgSentence = "%1$s %2$d %3$s";
  @Expose
  @SerializedName("SourceList")
  private List<String> mSourceList = new LinkedList<>();

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
    List<Environment> environments = Api.getEnvironments(Misc.iterableToString(mSourceList, true));
    for (Environment environment : environments) {
      List<Text> items = environment.getItems();
      checkItems(environment, items);
      checkSentence(items);
    }
  }

  private void checkItems(Environment environment, List<Text> items) {
    if (mMinItems > -1 && items.size() < mMinItems) {
      mLog.info(new Result(mName, environment.getPosition(), String.format(mMsgItem, "has less than", mMinItems, "items")).toString());
    }
    if (mMaxItems > -1 && items.size() > mMaxItems) {
      mLog.info(new Result(mName, environment.getPosition(), String.format(mMsgItem, "has more than", mMaxItems, "items")).toString());
    }
  }

  private void checkSentence(List<Text> items) {
    for (Text item : items) {
      String cText = item.getMasked();
      List<String> sentences = Misc.getSentences(cText, false);

      if (mMinSentences > -1 && sentences.size() < mMinSentences) {
        mLog.info(new Result(mName, item.getPosition(), String.format(mMsgSentence, "has less than", mMinSentences, "sentences")).toString());
      }
      if (mMaxSentences > -1 && sentences.size() > mMaxSentences) {
        mLog.info(new Result(mName, item.getPosition(), String.format(mMsgSentence, "has more than", mMaxSentences, "sentences")).toString());
      }
    }
  }

  @Override
  public void validation() {
    try {
      String.format(mMsgSentence, "test", 10, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(ItemCheck.class.getName(), mName, e);
      mMsgSentence = "%1$s %2$d %3$s";
    }
    try {
      String.format(mMsgItem, "test", 10, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(ItemCheck.class.getName(), mName, e);
      mMsgItem = "%1$s %2$d %3$s";
    }
  }
}
