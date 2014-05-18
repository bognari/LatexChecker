package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.DocumentTree;
import de.tubs.latexTool.core.entrys.Result;

import java.util.IllegalFormatException;

/**
 * Testet ob eine Dokument zu viele verschachtelte Kapitel hat und ob jedes Kapitel zwischen min und max Unterkapitel
 * Besitzt
 * Wenn ein Kapitel keine Unterkapitel hat, dann ist er vom min ausgeschlossen
 * Testet ob jedes Kapitel zwischen min und max "Sätze" besitzt
 * Eingrenzungen mittels FromLevel und ToLevel möglich
 * <p></p>
 * MinSubChapters<br></br>
 * MaxSubChapters<br></br>
 * MaxChapterDepth<br></br>
 * MinSentences<br></br>
 * MaxSentences<br></br>
 * MsgSubChapters<br></br>
 * MsgChapters<br></br>
 * MsgSentences<br></br>
 * FromLevel<br></br>
 * ToLevel<br></br>
 */
public class ChapterCheck extends Module {

  @Expose
  @SerializedName("FromLevel")
  private int mFromLevel = -19;
  @Expose
  @SerializedName("MaxChapterDepth")
  private int mMaxChapterDepth = -1;
  @Expose
  @SerializedName("MaxSentences")
  private int mMaxSentences = -1;
  @Expose
  @SerializedName("MaxSubChapters")
  private int mMaxSubChapters = -1;
  @Expose
  @SerializedName("MinSentences")
  private int mMinSentences = -1;
  @Expose
  @SerializedName("MinSubChapters")
  private int mMinSubChapters = -1;
  @Expose
  @SerializedName("MsgChapters")
  private String mMsgChapters = "%1$s %2$s %3$d %4$s (%5$d)";
  @Expose
  @SerializedName("MsgSentences")
  private String mMsgSentences = "%1$s %2$s %3$d %4$s (%5$d)";
  @Expose
  @SerializedName("MsgSubChapters")
  private String mMsgSubChapters = "%1$s %2$s %3$d %4$s (%5$d)";
  @Expose
  @SerializedName("ToLevel")
  private int mToLevel = Integer.MAX_VALUE;

  private void checkChapterDepth(DocumentTree tree, int length) {
    if (tree.getLevel() > -20) {
      if (length < 1) {
        mLog.info(new Result(mName, tree.getPosition(), String.format(mMsgChapters, tree.getHeadline(), "is deeper as", mMaxChapterDepth, "chapters", (mMaxChapterDepth + Math.abs(length) + 1))).toString());
      }
    }
    for (DocumentTree child : tree.child()) {
      checkChapterDepth(child, length - 1);
    }
  }

  private void checkSentences(DocumentTree tree) {
    if (tree.getLevel() > -20 && tree.getLevel() >= mFromLevel && tree.getLevel() <= mToLevel) {
      if (mMinSentences > 0 && tree.getTexts().size() < mMinSentences) {
        mLog.info(new Result(mName, tree.getPosition(), String.format(mMsgSentences, tree.getHeadline(), "has less than", mMinSentences, "sentences", tree.getTexts().size())).toString());
      }
      if (mMaxSentences > -1 && tree.getTexts().size() > mMaxSentences) {
        mLog.info(new Result(mName, tree.getPosition(), String.format(mMsgSentences, tree.getHeadline(), "has more than", mMaxSentences, "sentences", tree.getTexts().size())).toString());
      }
    }
    for (DocumentTree child : tree.child()) {
      checkSubChapters(child);
    }
  }

  private void checkSubChapters(DocumentTree tree) {
    if (tree.getLevel() > -20 && tree.getLevel() >= mFromLevel && tree.getLevel() <= mToLevel) {
      if (mMinSubChapters > 0 && !tree.child().isEmpty() && tree.child().size() < mMinSubChapters) {
        mLog.info(new Result(mName, tree.getPosition(), String.format(mMsgSubChapters, tree.getHeadline(), "has less than", mMinSubChapters, "subchapters", tree.child().size())).toString());
      }
      if (mMaxSubChapters > -1 && tree.child().size() > mMaxSubChapters) {
        mLog.info(new Result(mName, tree.getPosition(), String.format(mMsgSubChapters, tree.getHeadline(), "has more than", mMaxSubChapters, "subchapters", tree.child().size())).toString());
      }
    }
    for (DocumentTree child : tree.child()) {
      checkSubChapters(child);
    }
  }

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
    if (!(mMinSubChapters <= -1 && mMaxSubChapters <= -1)) {
      checkSubChapters(Api.getDocumentTreeRoot());
    }
    if (mMaxChapterDepth > -1) {
      checkChapterDepth(Api.getDocumentTreeRoot(), mMaxChapterDepth);
    }
    if (!(mMinSentences <= -1 && mMaxSentences <= -1)) {
      checkSentences(Api.getDocumentTreeRoot());
    }

  }

  @Override
  protected void validation() {
    try {
      String.format(mMsgSubChapters, "test", "test", 10, "test", 10);
    } catch (IllegalFormatException e) {
      mLog.throwing(ChapterCheck.class.getName(), mName, e);
      mMsgSubChapters = "%1$s %2$s %3$d %4$s (%5$d)";
    }
    try {
      String.format(mMsgChapters, "test", "test", 10, "test", 10);
    } catch (IllegalFormatException e) {
      mLog.throwing(ChapterCheck.class.getName(), mName, e);
      mMsgChapters = "%1$s %2$s %3$d %4$s (%5$d)";
    }
    try {
      String.format(mMsgSentences, "test", "test", 10, "test", 10);
    } catch (IllegalFormatException e) {
      mLog.throwing(ChapterCheck.class.getName(), mName, e);
      mMsgSentences = "%1$s %2$s %3$d %4$s (%5$d)";
    }
  }
}
