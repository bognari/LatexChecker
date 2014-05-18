package de.tubs.latexTool.core;

import de.tubs.latexTool.core.config.Settings;
import de.tubs.latexTool.core.entrys.*;

import java.util.List;

/**
 * Die API ist das "Interface" für das Framework, alle Module sollen nur auf static Methoden aus der API zugreifen
 */
public abstract class Api {

  private static App mApp;

  /**
   * Gibt alle Überschriften des Dokuments zurück
   *
   * @return alle Überschriften
   */
  public static List<Text> allHeadlines() {
    return mApp.getTex().allHeadlines();
  }

  /**
   * Gibt alle Paragrafen des Dokuments zurück
   *
   * @return alle Paragrafen
   */
  public static List<Paragraph> allParagraphs() {
    return mApp.getTex().allParagraphs();
  }

  /**
   * Gibt alle Sätze des Dokuments zurück
   *
   * @return alle Sätze
   */
  public static List<Text> allTexts() {
    return mApp.getTex().allTexts();
  }

  /**
   * Gibt die App zurück (braucht man nie)
   *
   * @return
   */
  public static App getApp() {
    return mApp;
  }

  /**
   * Setzt die App (verwendet ihr nie)
   *
   * @param app
   */
  static void setApp(App app) {
    Api.mApp = app;
  }

  /**
   * Gibt eine Liste aller Befehleseinträge zurück
   *
   * @param pattern
   * @return
   */
  public static List<Command> getCommands(String pattern) {
    return mApp.getTex().getCommandList(pattern);
  }

  /**
   * Gibt den Rootknoten zurück
   *
   * @return
   */
  public static DocumentTree getDocumentTreeRoot() {
    return mApp.getTex().getDocumentTree();
  }

  /**
   * Gibt eine Liste aller
   *
   * @param pattern
   * @return
   */
  public static List<Environment> getEnvironments(String pattern) {
    return mApp.getTex().getEnvironments(pattern);
  }

  /**
   * Gibt die Position in den Latex Dateien zurück
   *
   * @param position die Position in Zeichen
   * @return
   */
  public static Position getPosition(int position) {
    return mApp.getTex().getPosition(position);
  }

  /**
   * Gibt den ganzen roh Content zurück
   *
   * @return
   */
  public static String getRawContent() {
    return mApp.getTex().getContent();
  }

  public static Settings settings() {
    return mApp.SettingObject();
  }
}
