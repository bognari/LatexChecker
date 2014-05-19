package de.tubs.latexTool.core.entrys;

import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.DocumentTree;
import de.tubs.latexTool.core.util.Misc;

public class Text implements IPosition {

  /**
   * Gibt die Verschiebung der Position an
   */
  private final int mOffset;

  private final Paragraph mParagraph;
  /**
   * mStart des Satzes
   */
  private final int mStart;
  /**
   * der Text
   */
  private final String mText;

  /**
   * Erzeugt einen Textschnipsel
   *
   * @param text
   * @param start
   * @param offset
   */
  public Text(String text, int start, int offset) {
    this(text, null, start, offset);
  }

  /**
   * Erzeugt einen Satzschnipsel
   *
   * @param text
   * @param paragraph
   * @param start
   * @param offset
   */
  public Text(String text, Paragraph paragraph, int start, int offset) {
    mText = text.trim();
    mParagraph = paragraph;
    mStart = start;
    mOffset = offset;
  }

  /**
   * Gibt den DocumentTree Knoten zurück, wenn verfügbar, sonst NULL
   *
   * @return
   */
  public DocumentTree getDocumentTree() {
    return mParagraph == null ? null : mParagraph.getNode();
  }

  /**
   * Gibt den Inhalt ohne Latex zurück
   *
   * @return
   */
  public String getMasked() {
    String ret = mText;
    ret = Misc.removeLatex(ret);
    ret = ret.trim();
    ret = ret.replaceAll("\\s+", " ");
    return ret;
  }

  public int getOffset() {
    return mOffset;
  }

  public Paragraph getParagraph() {
    return mParagraph;
  }

  public String getText() {
    return mText;
  }

  @Override
  public int hashCode() {
    int result = mOffset;
    result = 31 * result + (mParagraph != null ? mParagraph.hashCode() : 0);
    result = 31 * result + mStart;
    result = 31 * result + mText.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Text text1 = (Text) o;

    if (mOffset != text1.mOffset) return false;
    if (mStart != text1.mStart) return false;
    if (mParagraph != null ? !mParagraph.equals(text1.mParagraph) : text1.mParagraph != null) return false;
    return mText.equals(text1.mText);

  }

  @Override
  public String toString() {
    return mText;
  }

  public boolean isSentence() {
    return !mText.isEmpty() && mText.split(" ").length > 2;
  }

  public int getEnd() {
    return mOffset + mStart + mText.length();
  }


  @Override
  public Position getEndPosition() {
    return Api.getPosition(getEnd());
  }


  public Position getPosition() {
    return Api.getPosition(mStart + mOffset);
  }

  public int getStart() {
    return mOffset + mStart;
  }


}