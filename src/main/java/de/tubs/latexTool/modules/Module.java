package de.tubs.latexTool.modules;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

import java.util.logging.Logger;

/**
 * Diese Klasse soll als Elter für alle Module gelten
 */
public abstract class Module implements Runnable {

  private static final Gson sGson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
  private final static Logger sLog = Logger.getLogger(Module.class.getName());
  protected Logger mLog;
  protected String mName;

  /**
   * Diese Methode ist eine Factory für Module. Es wird die Class Option gelesen und dann versucht die Klasse über
   * den Classloader zuladen
   *
   * @param name   Name des Moduls (hat nichts mit der Klasse zutun und wird nur als Superschlüssel im Log verwendet)
   * @param config Die Konfiguration als Map aus den Json Dateien
   * @return das fertig konfigurierte und startbereite Modul
   * @throws java.lang.IllegalArgumentException wenn die Class Option fehlt, die Klasse nicht geladen werden kann
   *                                            oder wenn die Klasse nicht vom Typ Modul ist
   */
  public final static Module getModul(String name, JsonObject config) {
    if (!config.has("Class")) {
      sLog.severe(String.format("Modul = %s has no Class definition", name));
      throw new IllegalArgumentException();
    }

    String modulClass = config.getAsJsonPrimitive("Class").getAsString();

    Class<?> clazz;

    try {
      clazz = Class.forName(modulClass);
    } catch (ClassNotFoundException e) {
      sLog.throwing(Module.class.getName(), "getModul", e);
      throw new IllegalArgumentException();
    }

    if (!(Module.class.isAssignableFrom(clazz))) {
      sLog.severe(String.format("The Class in Modul %s is not a Modul!", name));
      throw new IllegalArgumentException();
    }

    Module module = (Module) sGson.fromJson(config, clazz);
    module.mName = name;
    module.mLog = Logger.getLogger(Module.class.getName() + "." + name);
    module.validation();
    return module;
  }

  /**
   * In diese Methode können die Parameter, die das Modul übergeben bekommen hat, auf Richtigkeit getestet werden usw.
   */
  protected void validation() {
  }

  @Override
  public String toString() {
    return String.format(mName);
  }
}
