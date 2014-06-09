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
  /**
   * "ABBREVIATIONS" : {"Sprache" : ["Abkürzung"]} <p></p>
   * Eine Map, für eine Zusammenfassung aller Abkürzungen zu einer Sprache
   */
  @Expose
  @SerializedName("ABBREVIATIONS")
  Map<String, List<String>> mAbb;
  /**
   * "COMMAND_BREAK_LATEX" : ["Befehl"] <p></p>
   * Eine Liste aller Befehle, die den normalen Latex Modul verlassen, z.b. "lstinline"
   */
  @Expose
  @SerializedName("COMMAND_BREAK_LATEX")
  List<String> mBadCom;
  /**
   * "ENVIRONMENT_BREAK_LATEX" : ["Umgebung"] <p></p>
   * Eine Liste aller Umgebungen, die den normalen Latex Modul verlassen, z.b. "verbatim"
   */
  @Expose
  @SerializedName("ENVIRONMENT_BREAK_LATEX")
  List<String> mBadEnv;
  /**
   * "COMMAND_WHITELIST" : {"Befehl": [Index der zu übernehmenden Parameter]} <p></p>
   * Eine Whitelist für Befehle, deren Inhalt zum Text dazu gezählt wird.<p></p>
   * 1 Bedeutet der erste Parameter, -1 der Letzte, somit bedeutet [1,3,-3], das der erste, dritte und drittletzte
   * übernommen wird,
   * wenn die Liste leer ist, werden alle Parameter übernommen.
   */
  @Expose
  @SerializedName("COMMAND_WHITELIST")
  Map<String, List<Integer>> mCommandWhitelist;
  /**
   * "CONVERTING" : {"Latex" : "Übersetzung"} <p></p>
   * Eine Map, die die Übersetzung für Latexbefehle enthält, diese wird beim Einlesen der Datei verwendet. z.B.: "a ->
   * ä
   */
  @Expose
  @SerializedName("CONVERTING")
  Map<String, String> mConverting;
  /**
   * "ENVIRONMENT_WHITELIST" : ["Umgebung"] <p></p>
   * Eine Whitelist für Umgebungen, deren Inhalt zum Text dazu gezählt wird.
   */
  @Expose
  @SerializedName("ENVIRONMENT_WHITELIST")
  List<String> mEnvironmentWhitelist;
  /**
   * "ITEM_ENVIRONMENTS" : ["Umgebung"] <p></p>
   * Eine Liste von Umgebungen, die Items enthalten können
   */
  @Expose
  @SerializedName("ITEM_ENVIRONMENTS")
  List<String> mItemEnvironments;
  /**
   * "LATEX" : {"Latex" : "Übersetzung"} <p></p>
   * Eine Map, die die Übersetzung für Latexbefehle enthält, diese wird beim Auswerten der Datei verwendet. z.B.:
   * \\dots
   * -> dots
   */
  @Expose
  @SerializedName("LATEX")
  Map<String, String> mLatex;
  /**
   * "MATH_ENVIRONMENTS" : ["Umgebung"] <p></p>
   * Eine Liste von Umgebungen, für den Mathe Modus, diese werden mit "$Inhalt$" interpretiert im Text Modus.
   */
  @Expose
  @SerializedName("MATH_ENVIRONMENTS")
  List<String> mMathEnvironments;
  /**
   * "RULES" : {"Rule" : {"Class" : "Modul"}} <p></p>
   * Eine "Liste" von Regeln, die durch die jeweiligen Module umgesetzt wird, jede Regel besitzt einen eigenen Namen
   * (bei "Rule") und muss mindestens die Klasse des Moduls bei "Modul" angeben, durch die sie um gesetzt wird.
   */
  @Expose
  @SerializedName("RULES")
  Map<String, JsonObject> mModules;
  /**
   * "PARTS" : {"Part" : Level} <p></p>
   * Eine Map, die die benutzten Sections-Befehle und deren Level angibt. <p></p>
   * {"part": -10, "chapter": 0, "section": 10, "subsection": 20, "subsubsection": 30, "paragraph": 40, "subparagraph":
   * 50}
   */
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

