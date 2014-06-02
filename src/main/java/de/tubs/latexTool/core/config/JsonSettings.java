package de.tubs.latexTool.core.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;
import java.util.Map;

/**
 * Diese Klasse Verwaltet alle Einstellungen
 */
class JsonSettings {
  @Expose
  @SerializedName("ABB")
  Map<String, List<String>> mAbb;
  @Expose
  @SerializedName("COMMAND_BREAK_COMMENT")
  List<String> mBadCom;
  @Expose
  @SerializedName("ENVIRONMENT_BREAK_COMMENT")
  List<String> mBadEnv;
  @Expose
  @SerializedName("Converting")
  Map<String, String> mConverting;
  @Expose
  @SerializedName("ENVIRONMENTS")
  List<String> mEnvironments;
  @Expose
  @SerializedName("ITEM_ENVIRONMENTS")
  List<String> mItemEnvironments;
  @Expose
  @SerializedName("LATEX")
  Map<String, String> mLatex;
  @Expose
  @SerializedName("LATEXWHITELIST")
  Map<String, List<Integer>> mLatexWhitelist;
  @Expose
  @SerializedName("MATH_ENVIRONMENTS")
  List<String> mMathEnvironments;
  @Expose
  @SerializedName("MODULES")
  Map<String, JsonObject> mModules;
  @Expose
  @SerializedName("PARTS")
  Map<String, Integer> mParts;
  /**
   * eine Repräsentation der Einstellungen als Json
   */
  private JsonObject mJSettings;

  /**
   * Vereinigt das Source JsonObject ins Target JsonObject und gibt dann das veränderte Target zurück
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

  public JsonObject getJSettings() {
    return mJSettings;
  }

  public void setJSettings(JsonObject JSettings) {
    mJSettings = JSettings;
  }
}

