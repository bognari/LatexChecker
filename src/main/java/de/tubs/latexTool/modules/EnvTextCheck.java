package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.DocumentTree;
import de.tubs.latexTool.core.entrys.Environment;
import de.tubs.latexTool.core.entrys.IPosition;
import de.tubs.latexTool.core.entrys.Result;
import de.tubs.latexTool.core.entrys.Text;
import de.tubs.latexTool.core.util.Misc;

import java.util.*;

/**
 * Created by stephan on 12.05.14.
 */
public class EnvTextCheck extends Module {
  @Expose
  @SerializedName("EnvList")
  private List<String> mEnvList = new LinkedList<>();
  private Map<Integer, IPosition> mMap = new TreeMap<>();
  /*@Expose
  @SerializedName("MaxAfterSentences")
  private int mMaxAfterSentences = -1;*/
  @Expose
  @SerializedName("MinAfterSentences")
  private int mMinAfterSentences = -1;
  /*@Expose
  @SerializedName("MaxBeforeSentences")
  private int mMaxBeforeSentences = -1;*/
  @Expose
  @SerializedName("MinBeforeSentences")
  private int mMinBeforeSentences = -1;
  /*@Expose
  @SerializedName("MaxBetweenSentences")
  private int mMaxBetweenSentences = -1;*/
  @Expose
  @SerializedName("MinBetweenSentences")
  private int mMinBetweenSentences = -1;
  @Expose
  @SerializedName("Msg")
  private String mMsg = "%1$s %2$d %3$s";

  private boolean isIn(IPosition env) {
    for (int key : mMap.keySet()) {
      IPosition position = mMap.get(key);
      if (position.getStart() <= env.getStart() && env.getEnd() <= position.getEnd()) {
        return true;
      }
    }
    return false;
  }

  private void readEnvs() {
    List<Environment> environmentList = Api.getEnvironments(Misc.iterableToString(mEnvList, true));

    for (Environment environment : environmentList) {
      if (!isIn(environment)) {
        mMap.put(environment.getStart(), environment);
      }
    }
  }

  private void readSections() {
    DocumentTree root = Api.getDocumentTreeRoot();
    sectionRunner(root);
  }

  private void readText() {
    List<Text> textList = Api.allTexts();
    for (Text text : textList) {
      mMap.put(text.getStart(), text);
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
    readEnvs();
    readSections();
    readText();
    runTest();
  }

  private void runTest() {
    int sinceSection = -1;
    int sinceEnv = -1;
    for (int key : mMap.keySet()) {
      IPosition position = mMap.get(key);
      if (position instanceof DocumentTree) {
        if (sinceEnv > -1) {
          if (mMinAfterSentences > -1 && sinceEnv < mMinAfterSentences) {
            mLog.info(new Result(mName, position.getPosition(), String.format(mMsg, "has less than", mMinAfterSentences, "sentences after the last environment")).toString());
          }
          /*if (mMaxAfterSentences > -1 && sinceEnv > mMaxAfterSentences) {
            mLog.info(new Result(mName, position.getPosition(), String.format(mMsg, "has more than", mMaxAfterSentences, "sentences after the last environment")).toString());
          }*/
        }
        sinceSection = 0;
      }
      if (position instanceof Environment) {
        if (sinceSection > -1) {
          if (mMinBeforeSentences > -1 && sinceSection < mMinBeforeSentences) {
            mLog.info(new Result(mName, position.getPosition(), String.format(mMsg, "has less than", mMinBeforeSentences, "sentences before the next environment")).toString());
          }

          /*if (mMaxBeforeSentences > -1 && sinceSection > mMaxBeforeSentences) {
            mLog.info(new Result(mName, position.getPosition(), String.format(mMsg, "has more than", mMaxBeforeSentences, "sentences before the next environment")).toString());
          }*/
        }
        sinceSection += mMinBeforeSentences;
        if (sinceEnv > -1) {
          if (mMinBetweenSentences > -1 && sinceEnv < mMinBetweenSentences) {
            mLog.info(new Result(mName, position.getPosition(), String.format(mMsg, "has less than", mMinBetweenSentences, "sentences between the last environment")).toString());
          }
          /*if (mMaxBetweenSentences > -1 && sinceEnv > mMaxBetweenSentences) {
            mLog.info(new Result(mName, position.getPosition(), String.format(mMsg, "has more than", mMaxBetweenSentences, "sentences between the last environment")).toString());
          }*/
        }
        sinceEnv = 0;
      }
      if (position instanceof Text) {
        sinceEnv++;
        sinceSection++;
      }
    }
  }

  private void sectionRunner(DocumentTree node) {
    mMap.put(node.getStart(), node);
    for (DocumentTree child : node.child()) {
      sectionRunner(child);
    }
  }

  @Override
  protected void validation() {
    try {
      String.format(mMsg, "test", 10, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(ChapterCheck.class.getName(), mName, e);
      mMsg = "%1$s %2$d %3$s";
    }
  }
}
