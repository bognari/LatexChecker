package de.tubs.latexTool.core;


import com.jdotsoft.jarloader.JarClassLoader;

public final class MyAppLauncher {

  private MyAppLauncher() {
  }

  public static void main(String[] args) {
    JarClassLoader jcl = new JarClassLoader();
    try {
      jcl.invokeMain("de.tubs.latexTool.core.App", args);
    } catch (Throwable e) {
      e.printStackTrace();
    }
  }
}