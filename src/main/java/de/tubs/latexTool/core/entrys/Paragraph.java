package de.tubs.latexTool.core.entrys;

import de.tubs.latexTool.core.DocumentTree;

import java.util.LinkedList;
import java.util.List;

/**
 * Diese Klasse stellt einen klassischen Paragraphen da, dieser besitzt eine bestimmte Anzahl an Sätzen und endet mit
 * einem \\ oder \n\n im Text
 */
public class Paragraph {
  private final DocumentTree mNode;
  private final List<Text> mTextList = new LinkedList<>();

  public Paragraph(DocumentTree node) {
    mNode = node;
  }

  public DocumentTree getNode() {
    return mNode;
  }

  public List<Text> getTexts() {
    return mTextList;
  }

  @Override
  public int hashCode() {
    int result = mNode != null ? mNode.hashCode() : 0;
    result = 31 * result + mTextList.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Paragraph paragraph = (Paragraph) o;

    if (mNode != null ? !mNode.equals(paragraph.mNode) : paragraph.mNode != null) return false;
    return mTextList.equals(paragraph.mTextList);

  }

  @Override
  public String toString() {
    return String.format("%d sentences | %s", mTextList.size(), getPosition());
  }

  public Position getPosition() {
    if (!mTextList.isEmpty()) {
      return mTextList.get(0).getPosition();
    }
    return mNode.getPosition();
  }
}
