package de.tubs.latexTool.core.entrys;

import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.ChapterTree;

public class Headline implements IPosition {

  /**
   * Ende der Überschrift
   */
  private final int mEnd;
  /**
   * headline
   */
  private final String mHeadline;
  /**
   * short headline
   */
  private final String mShortHeadline;
  /**
   * Start der Überschrift
   */
  private final int mStart;
  private ChapterTree mChapterTree;

  public Headline(Command part) {
    mStart = part.getStart();
    mEnd = part.getEnd();
    switch (part.getArgs().size()) {
      case 1:
        mHeadline = part.getArgs().get(0);
        mShortHeadline = null;
        break;
      case 2:
        mShortHeadline = part.getArgs().get(0);
        mHeadline = part.getArgs().get(1);
        break;
      default:
        mShortHeadline = null;
        mHeadline = null;
        throw new IllegalStateException("crazy part command");
    }
  }

  /**
   * Gibt den ChapterTree Knoten zurück, wenn verfügbar, sonst NULL
   *
   * @return
   */
  public ChapterTree getChapterTree() {
    return mChapterTree;
  }

  public void setChapterTree(ChapterTree chapterTree) {
    mChapterTree = chapterTree;
  }

  @Override
  public int getEnd() {
    return mEnd;
  }

  @Override
  public Position getEndPosition() {
    return Api.getPosition(mEnd);
  }

  public Position getPosition() {
    return Api.getPosition(mStart);
  }

  public int getStart() {
    return mStart;
  }

  public String getHeadline() {
    return mHeadline;
  }

  public String getShortHeadline() {
    return mShortHeadline;
  }

  @Override
  public String toString() {
    return String.format("%s%s", mShortHeadline != null ? String.format("[%s] ", mShortHeadline) : "", mHeadline);
  }
}