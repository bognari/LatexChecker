package de.tubs.latexTool.core.config;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.ParameterException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class Settings {
  private static final Gson sGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
  private static final Logger sLog = Logger.getLogger(JsonSettings.class.getName());
  private ArgsSettings mArgsSettings = new ArgsSettings();
  private JCommander mJc = new JCommander(mArgsSettings);
  private JsonSettings mJsonSettings;

  public Settings() throws IOException {
    defaultSetting();
  }

  /**
   * Setzt die default Settings aus der /conf/config.json in der Jar
   */
  private void defaultSetting() throws IOException {
    sLog.fine("defaultSetting reading defaults");
    final InputStream is = JsonSettings.class.getResourceAsStream("/conf/config.json");
    final InputStreamReader isr = new InputStreamReader(is, Charset.forName("UTF-8"));
    try (BufferedReader bufferedReader = new BufferedReader(isr)) {
      final JsonParser jsonParser = new JsonParser();
      final JsonObject jSettings = (JsonObject) jsonParser.parse(bufferedReader);
      mJsonSettings = sGson.fromJson(jSettings, JsonSettings.class);
      mJsonSettings.setJSettings(jSettings);
      sLog.config(String.format("loadJson new config is = %s", jSettings.toString()));
      sLog.fine("defaultSettings finish reading defaults");
    } catch (IOException e) {
      sLog.throwing(JsonSettings.class.getName(), "defaultSetting", e);
      throw e;
    }
  }

  /**
   * Gibt eine Map mit allen bekannten Abkürzungen für alle bekannten (in der Json vorhanden) Sprachen zurück
   *
   * @return
   */
  public Map<String, List<String>> getAbbs() {
    return mJsonSettings.getAbb();
  }

  /**
   * Gibt die Liste von Befehlen, welche die Latex kommentare zerstören zurück
   *
   * @return
   */
  public List<String> getBadCom() {
    return mJsonSettings.getBadCom();
  }

  /**
   * Gibt die Liste von Environments, welche die Latex kommentare zerstören zurück
   *
   * @return
   */
  public List<String> getBadEnv() {
    return mJsonSettings.getBadEnv();
  }

  public String getCharset() {
    return mArgsSettings.getCharset();
  }

  public List<String> getConfigs() {
    return mArgsSettings.getConfigs();
  }

  public String getDefaultConfig() {
    return mArgsSettings.getDefaultConfig();
  }

  /**
   * Gibt eine Menge aller auszuwertenden Umgebungen zurück
   *
   * @return
   */
  public List<String> getEnvironments() {
    return mJsonSettings.getEnvironments();
  }

  public List<String> getItemEnvironments() {
    return mJsonSettings.getItemEnvironments();
  }

  public String getLanguage() {
    return mArgsSettings.getLanguage();
  }

  /**
   * Gibt eine Latex Übersetztungstabelle zurück
   *
   * @return
   */
  public Map<String, String> getLatexTranslation() {
    return mJsonSettings.getLatex();
  }

  public String getLogLevel() {
    return mArgsSettings.getLogLevel();
  }

  /**
   * Gibt eine Menge aller auszuwertenden Mathumgebungen zurück
   *
   * @return
   */
  public List<String> getMathEnvironments() {
    return mJsonSettings.getMathEnvironments();
  }

  /**
   * Gibt eine Map mit allen Modulnamen und deren Einstellungen zurück, jedes Modul soll selbst auf die richtigen
   * Typen usw achten!
   *
   * @return
   */
  public Map<String, JsonObject> getModules() {
    return mJsonSettings.getModules();
  }

  /**
   * Gibt alle vom Benutzter eingefügten Hierarchie Befehlen als Map zurück
   *
   * @return
   */
  public Map<String, Integer> getParts() {
    return mJsonSettings.getParts();
  }

  /**
   * Gibt den Pfad zur Latex Root Datei zurück
   *
   * @return
   */
  public String getSource() {
    return mArgsSettings.getSource();
  }

  /**
   * Gibt eine Menge aller auszuwertenden Latexbefehlen zurück
   *
   * @return
   */
  public Map<String, List<Integer>> getWhiteList() {
    return mJsonSettings.getWhiteList();
  }

  public boolean isHasNoDocumentEnv() {
    return mArgsSettings.isHasNoDocumentEnv();
  }

  public boolean isHelp() {
    return mArgsSettings.isHelp();
  }

  public boolean isLicense() {
    return mArgsSettings.isLicense();
  }

  public boolean isNewline() {
    return mArgsSettings.isNewline();
  }

  public boolean isThirdParty() {
    return mArgsSettings.isThirdParty();
  }

  public boolean isUseAbbreviationsEscaping() {
    return mArgsSettings.isUseAbbreviationsEscaping();
  }

  public boolean isVerbose() {
    return mArgsSettings.isVerbose();
  }

  public boolean isVersion() {
    return mArgsSettings.isVersion();
  }

  /**
   * Ändert die Settings und merget die neuen Settings in die alten.
   *
   * @param config Pfad zur Json Datei
   */
  public void loadJson(String config) throws IOException {
    sLog.fine(String.format("loadJson start reading file = %s", config));
    try (BufferedReader bufferedReader = Files.newBufferedReader(Paths.get(config), Charset.forName("UTF-8"))) {
      final JsonParser jsonParser = new JsonParser();
      final JsonObject jSettings = JsonSettings.merge((JsonObject) jsonParser.parse(bufferedReader), mJsonSettings.getJSettings());
      mJsonSettings = sGson.fromJson(jSettings, JsonSettings.class);
      mJsonSettings.setJSettings(jSettings);
      sLog.config(String.format("loadJson new config is = %s", jSettings.toString()));
      sLog.fine("loadJson finish reading defaults");
    } catch (IOException e) {
      sLog.throwing(JsonSettings.class.getName(), "loadJson", e);
      throw e;
    }
  }

  /**
   * parst die Parameter
   *
   * @param args die von der main
   */
  public void parse(String[] args) throws ParameterException {
    try {
      mJc.parse(args);
    } catch (ParameterException e) {
      System.out.println(e.getLocalizedMessage());
      System.out.println();
      mJc.usage();
      throw e;
    }
  }

  public void usage() {
    mJc.usage();
  }
}
