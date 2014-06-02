package de.tubs.latexTool.core;

import de.tubs.latexTool.core.entrys.ILatex;

/**
 * Gibt an wenn es kein valider Latex Code ist (oder das Programm denkt das es so ist)
 */
public class LatexException extends Exception {

  public LatexException(ILatex latex) {
    this(latex, "");
  }

  public LatexException(ILatex latex, String string) {
    super(String.format("%s %s", msg(latex), string).trim());
    System.out.println(getMessage());
  }

  private static String msg(ILatex latex) {
    return String.format("can not parse %s (%s) at %s", latex.getName(), latex.getClass().getSimpleName(), latex.getPosition());
  }


}
