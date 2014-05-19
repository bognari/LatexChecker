package de.tubs.latexTool.core;

import com.beust.jcommander.ParameterException;
import com.google.gson.JsonObject;
import de.tubs.latexTool.core.config.Settings;
import de.tubs.latexTool.core.util.LogHandler;
import de.tubs.latexTool.modules.Module;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


public final class App {

  private final Logger mLog = Logger.getLogger("de.tubs.latexTool");
  private final LogHandler mLogHandler = new LogHandler();
  private Settings mSettings;
  private Tex mTex;

  /**
   * Erstellt die App und bereitet diese für den Einsatz vor
   */
  public App() throws IOException {
    Api.setApp(this);

    mLog.addHandler(mLogHandler);
    mLog.setUseParentHandlers(false);

    try {
      mSettings = new Settings();
    } catch (IOException e) {
      mLog.severe("can not load default Settings from jar /conf/config.json");
      throw e;
    }
  }

  /**
   * Startet die App
   *
   * @param args die Aufrufparameter
   */
  public static void main(String[] args) {
    App app;
    try {
      app = new App();
    } catch (IOException e) {
      return;
    }

    try {
      app.mSettings.parse(args);
    } catch (ParameterException e) {
      return;
    }
    app.run();

    app.runModules();

    app.printLog();
  }

  /**
   * verhält sich der Konfiguration nach
   */
  private void run() {
    if (mSettings.isHelp()) {
      mSettings.usage();
    }
    if (mSettings.isVersion()) {
      Package objPackage = App.class.getPackage();

      String name = objPackage.getSpecificationTitle();
      String version = objPackage.getSpecificationVersion();
      String vendor = objPackage.getSpecificationVendor();

      System.out.println("Package name: " + name);
      System.out.println("Package version: " + version);
      System.out.println("Package vendor: " + vendor);
      System.out.println();
    }

    if (mSettings.isLicense() || mSettings.isVersion()) {
      InputStream is = getClass().getResourceAsStream("/license.txt");
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String line;
      try {
        while ((line = br.readLine()) != null) {
          System.out.println(line);
        }
        System.out.println();
      } catch (IOException e) {
        mLog.throwing(App.class.getName(), "run", e);
      }
    }

    if (mSettings.isThirdParty() || mSettings.isVersion()) {
      InputStream is = getClass().getResourceAsStream("/thirdparty.txt");
      BufferedReader br = new BufferedReader(new InputStreamReader(is));
      String line;
      try {
        System.out.println("third party software:");
        while ((line = br.readLine()) != null) {
          System.out.println(line);
        }
        System.out.println();
      } catch (IOException e) {
        mLog.throwing(App.class.getName(), "run", e);
      }
    }

    mLog.setLevel(Level.parse(mSettings.getLogLevel()));

    if (!mSettings.getDefaultConfig().isEmpty()) {
      InputStream is = getClass().getResourceAsStream("/conf/defaultConfig.json");
      try {
        Files.copy(is, Paths.get(mSettings.getDefaultConfig()));
      } catch (IOException e) {
        mLog.throwing(App.class.getName(), "run", e);
      }
    }

    if (!mSettings.getConfigs().isEmpty()) {
      for (String path : mSettings.getConfigs()) {
        try {
          mSettings.loadJson(path);
        } catch (IOException e) {
          mLog.severe(String.format("can not load config file %s", path));
        }
      }
    }
  }

  /**
   * Arbeitet die Module aus der config.json ab
   */
  void runModules() {

    if (mSettings.getConfigs().isEmpty() || mSettings.getSource().isEmpty() || mSettings.getModules().isEmpty()) {
      return;
    }

    Map<String, JsonObject> modules = mSettings.getModules();
    final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(modules.size(), modules.size(), 10, TimeUnit.SECONDS, new LinkedTransferQueue<Runnable>());
    mLog.fine("start module workers");
    Timer timer = new Timer(true);
    if (mSettings.isVerbose()) {
      timer.scheduleAtFixedRate(new TimerTask() {
        @Override
        public void run() {
          String s = String.format("Module %d threads are running, (%d / %4$d) are completed and (%d  / %4$d) are queued", threadPool.getActiveCount(), threadPool.getCompletedTaskCount(), threadPool.getQueue().size(), threadPool.getTaskCount());
          mLog.finest(s);
          String t = String.format("Module: %3d%%", threadPool.getTaskCount() <= 0 ? 0 : (threadPool.getCompletedTaskCount() * 100) / threadPool.getTaskCount());
          mLog.finer(t);
          System.out.println(t);
        }
      }, 100, 500);
    }

    for (String name : modules.keySet()) {
      if (!name.startsWith("#")) {
        try {
          Module module = Module.getModul(name, modules.get(name));
          threadPool.execute(module);
        } catch (IllegalArgumentException e) {
          mLog.severe(String.format("Can not load Modul %s", name));
        }
      } else {
        mLog.fine(String.format("skip module: %s", name));
      }
    }

    threadPool.shutdown();
    try {
      threadPool.awaitTermination(2, TimeUnit.MINUTES);
    } catch (InterruptedException e) {
      mLog.throwing(App.class.getName(), "runModules", e);
    }
    timer.cancel();
    mLog.fine("finish module workers");
  }

  /**
   * gibt das Log aus
   */
  void printLog() {
    System.out.print(mLogHandler.getLogContent());
  }

  /**
   * gibt das Einstellungsobjekt zurück
   *
   * @return
   */
  public Settings SettingObject() {
    return mSettings;
  }

  /**
   * gibt die Tex Datei zurück
   *
   * @return
   */
  public Tex getTex() {
    if (mTex == null) {
      synchronized (this) {
        if (mTex == null) {
          loadTex();
        }
      }
    }
    return mTex;
  }

  /**
   * läd die Tex Datei
   */
  private void loadTex() {
    mTex = new Tex();
  }
}
