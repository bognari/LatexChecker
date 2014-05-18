package de.tubs.latexTool.core.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Diese Klasse Verwaltet alle Einstellungen
 */
public class JsonSettings {
  @Expose
  @SerializedName("ABB")
  private Map<String, List<String>> mAbb;
  @Expose
  @SerializedName("COMMAND_BREAK_COMMENT")
  private List<String> mBadCom;
  @Expose
  @SerializedName("ENVIRONMENT_BREAK_COMMENT")
  private List<String> mBadEnv;
  @Expose
  @SerializedName("ENVIRONMENTS")
  private List<String> mEnvironments;
  @Expose
  @SerializedName("ITEM_ENVIRONMENTS")
  private List<String> mItemEnvironments;
  /**
   * eine Repräsentation der Einstellungen als Json
   */
  private JsonObject mJSettings;
  @Expose
  @SerializedName("LATEX")
  private Map<String, String> mLatex;
  @Expose
  @SerializedName("LATEXWHITELIST")
  private Map<String, List<Integer>> mLatexWhitelist;
  @Expose
  @SerializedName("MATH_ENVIRONMENTS")
  private List<String> mMathEnvironments;
  @Expose
  @SerializedName("MODULES")
  private Map<String, JsonObject> mModules;
  @Expose
  @SerializedName("PARTS")
  private Map<String, Integer> mParts;

  /**
   * Merget das Source JsonObject ins Target JsonObject und gibt dann das veränderte Target zurück
   *
   * @param source die neue Json
   * @param target die alte Json
   * @return
   */
  static JsonObject merge(JsonObject source, JsonObject target) {
    for (Map.Entry<String, JsonElement> entry : source.entrySet()) {
      JsonElement value = source.get(entry.getKey());
      if (!target.has(entry.getKey())) {
        // new value for "key":
        target.add(entry.getKey(), entry.getValue());
      } else {
        // existing value for "key" - recursively deep merge:
        if (value instanceof JsonObject) {
          JsonObject valueJson = (JsonObject) value;
          merge(valueJson, target.getAsJsonObject(entry.getKey()));
        } else if (value instanceof JsonArray) {
          target.getAsJsonArray(entry.getKey()).addAll((JsonArray) value);
        } else {
          target.add(entry.getKey(), entry.getValue());
        }
      }
    }
    return target;
  }

  /**
   * Gibt eine Map mit allen bekannten Abkürzungen für alle bekannten (in der Json vorhanden) Sprachen zurück
   *
   * @return
   */
  public Map<String, List<String>> getAbb() {
    return Collections.unmodifiableMap(mAbb);
  }

  /**
   * Gibt die Liste von Befehlen, welche die Latex kommentare zerstören zurück
   *
   * @return
   */
  public List<String> getBadCom() {
    return Collections.unmodifiableList(mBadCom);
  }

  /**
   * Gibt die Liste von Environments, welche die Latex kommentare zerstören zurück
   *
   * @return
   */
  public List<String> getBadEnv() {
    return Collections.unmodifiableList(mBadEnv);
  }

  /**
   * Gibt eine Menge aller auszuwertenden Umgebungen zurück
   *
   * @return
   */
  public List<String> getEnvironments() {
    return Collections.unmodifiableList(mEnvironments);
  }

  public List<String> getItemEnvironments() {
    return Collections.unmodifiableList(mItemEnvironments);
  }

  public JsonObject getJSettings() {
    return mJSettings;
  }

  public void setJSettings(JsonObject JSettings) {
    this.mJSettings = JSettings;
  }

  /**
   * Gibt eine Latex Übersetztungstabelle zurück
   *
   * @return
   */
  public Map<String, String> getLatex() {
    return Collections.unmodifiableMap(mLatex);
  }

  /**
   * Gibt eine Menge aller auszuwertenden Mathumgebungen zurück
   *
   * @return
   */
  public List<String> getMathEnvironments() {
    return Collections.unmodifiableList(mMathEnvironments);
  }

  /**
   * Gibt eine Map mit allen Modulnamen und deren Einstellungen zurück, jedes Modul soll selbst auf die richtigen
   * Typen usw achten!
   *
   * @return
   */
  public Map<String, JsonObject> getModules() {
    return Collections.unmodifiableMap(mModules);
  }

  /**
   * Gibt alle vom Benutzter eingefügten Hierarchie Befehlen als Map zurück
   *
   * @return
   */
  public Map<String, Integer> getParts() {
    return Collections.unmodifiableMap(mParts);
  }

  /**
   * Gibt eine Menge aller auszuwertenden Latexbefehlen zurück
   *
   * @return
   */
  public Map<String, List<Integer>> getWhiteList() {
    return Collections.unmodifiableMap(mLatexWhitelist);
  }
}

