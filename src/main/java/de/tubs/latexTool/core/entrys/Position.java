package de.tubs.latexTool.core.entrys;

import java.nio.file.Path;

/**
 * Diese Klasse gilt als Positionsangabe
 */
public class Position {
  /**
   * Die Datei
   */
  private final Path mFile;
  /**
   * Die Zeilennummer
   */
  private final int mLine;

  /**
   * Erstellt ein LinenNumber Objekt
   *
   * @param file die Datei
   * @param line die Zeilennummer
   */
  public Position(Path file, int line) {
    mFile = file;
    mLine = line;
  }

  /**
   * Gibt die Datei zurück
   *
   * @return
   */
  public Path getFile() {
    return mFile;
  }

  /**
   * Gibt die Zeilennummer für die Datei zurück
   *
   * @return
   */
  public int getLine() {
    return mLine;
  }

  @Override
  public int hashCode() {
    int result = mFile.hashCode();
    result = 31 * result + mLine;
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

    Position position = (Position) o;

    if (mLine != position.mLine) {
      return false;
    }
    return mFile.equals(position.mFile);

  }

  @Override
  public String toString() {
    return "Position{" +
            "File=" + mFile +
            ", Line=" + mLine +
            '}';
  }
}