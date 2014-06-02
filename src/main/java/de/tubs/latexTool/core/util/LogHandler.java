package de.tubs.latexTool.core.util;

import de.tubs.latexTool.core.Api;

import java.util.*;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Diese Klasse verwaltet das Log und speichert alle Eintragungen
 */
public class LogHandler extends Handler {
  /**
   * in dieser Map werden alle Eintragungen gespeichert
   */
  private final Map<String, List<LogRecord>> mLogContent = new HashMap<>();

  /**
   * Gibt den Inhalt des ganzen Logs zurück
   *
   * @return
   */
  public String getLogContent() {
    StringBuilder stringBuilder = new StringBuilder();

    List<String> keys = new ArrayList<>(mLogContent.keySet());
    Collections.sort(keys);
    for (String modul : keys) {
      stringBuilder.append(String.format("Modul: %s%n%n", modul));
      for (LogRecord logRecord : mLogContent.get(modul)) {
        if (Api.settings().isNewline()) {
          stringBuilder.append(String.format("%2$s%n%n", logRecord.getLevel().getLocalizedName(), logRecord.getMessage()));
        } else {
          stringBuilder.append(String.format("%2$s%n", logRecord.getLevel().getLocalizedName(), logRecord.getMessage()));
        }
      }
      stringBuilder.append("#####################################################\n\n");
    }
    return stringBuilder.toString();
  }

  /**
   * Tragt eine Mitteilung in das Log ein
   *
   * @param record
   */
  public void publish(LogRecord record) {
    if (mLogContent.get(record.getLoggerName()) == null) {
      synchronized (mLogContent) {
        if (mLogContent.get(record.getLoggerName()) == null) {
          mLogContent.put(record.getLoggerName(), new LinkedList<LogRecord>());
        }
      }
    }
    // wie in gottes namen kann es hier einen nullpointer geben oO
    synchronized (mLogContent.get(record.getLoggerName())) {
      mLogContent.get(record.getLoggerName()).add(record);
    }
  }

  public void flush() {
  }

  public void close() {
  }
}
