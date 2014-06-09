package de.tubs.latexTool.core.entrys;

/**
 * Diese Klasse ist für den Logger gedacht und stellt eine Mitteilung an die "Außenwelt" dar
 */
public class Result {
  /**
   * Das erzeugende Modul
   */
  private final String mModul;
  /**
   * Die Meldung
   */
  private final String mMsg;
  /**
   * Die Position
   */
  private final Position mPosition;

  /**
   * Erzeugt eine neue "Meldung" für den Logger
   *
   * @param modul
   * @param position
   * @param msg
   */
  public Result(String modul, Position position, String msg) {
    mModul = modul;
    mPosition = position;
    mMsg = msg;
  }

  @Override
  public int hashCode() {
    int result = mModul.hashCode();
    result = 31 * result + mMsg.hashCode();
    result = 31 * result + mPosition.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    Result result = (Result) o;

    if (!mModul.equals(result.mModul)) {
      return false;
    }
    if (!mMsg.equals(result.mMsg)) {
      return false;
    }
    return mPosition.equals(result.mPosition);

  }

  @Override
  public String toString() {
    return String.format("%2$s at line :: %3$d in file :: %4$s", mModul, mMsg, mPosition.getLine(), mPosition.getFile());
  }
}
