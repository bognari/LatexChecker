package de.tubs.latexTool.core.util;

import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.LatexException;
import de.tubs.latexTool.core.entrys.Command;
import de.tubs.latexTool.core.entrys.Environment;
import org.languagetool.JLanguageTool;
import org.languagetool.Language;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Eine Sammlung von ganz tollen spaßigen Methoden,
 * eigentlich müssen alle 42 als "Antwort" liefern
 */
public final class Misc {

  /**
   * Pattern zum Suchen von Math Mode
   */
  public static final Pattern sMathMode = Pattern.compile("(?<!\\\\)\\$(?<Content>.*?)(?<!\\\\)\\$", Pattern.DOTALL);
  /**
   * Pattern für Befehle, die nur ein Argument besitzen, keine Verschachtelung erlaubt
   */
  //private static final String sCommand = "\\\\(?<Command>%s)(\\{(.*?)(?<!\\\\)\\}|\\|(.*?)\\||!(.*?)!)";
  private static final String sCommand = "(?<!\\\\)\\\\(?<Command>%s)";
  /**
   * Pattern zum Suchen von Environments
   */
  private static final Pattern sEnvironment = Pattern.compile("(?<!\\\\)\\\\(?<Begin>begin)\\s*\\{(?<Environment>.*?)\\}");
  /**
   * Pattern zum Suchen von Latex Befehlen
   */
  private static final Pattern sLatex = Pattern.compile("(?<!\\\\)\\\\(?<Command>(\\b([^\\s\\\\\\{\\[\\|!])+\\b)|(\\\\))", Pattern.DOTALL);
  private static final Logger sLog = Logger.getLogger(Misc.class.getName());
  /**
   * Pattern für die new und renew Befehle
   */
  private static final Pattern sReNew = Pattern.compile("(?<!\\\\)\\\\(?<Commands>((re)?new\\w*)(\\*)?)");
  /**
   * Pattern für Satzenden
   */
  private static final Pattern sSentenceEnd = Pattern.compile("(\\.|!|\\?|:)\\s*$");

  private Misc() {
  }

  /**
   * Wenn die Eingestellte Sprache "TEX_LANGUAGE" vom Languagetool unterstützt wird, wird eine Liste an Sätzen im
   * übergebenen "text" zurück gegeben
   *
   * @param text der Text
   * @return
   */
  public static List<String> getSentences(String text) {
    Language language;
    try {
      language = Language.getLanguageForShortName(Api.settings().getLanguage());
    } catch (IllegalArgumentException e) {
      sLog.throwing(Misc.class.getName(), "getSentences", e);
      sLog.severe(String.format("language %s is not supported", Api.settings().getLanguage()));
      return new LinkedList<>();
    }

    JLanguageTool jLanguageTool;
    try {
      jLanguageTool = new JLanguageTool(language);
    } catch (IOException e) {
      sLog.throwing(Misc.class.getName(), "getSentences", e);
      sLog.severe(String.format("can not load resources for the language %s", Api.settings().getLanguage()));
      return new LinkedList<>();
    }

    return jLanguageTool.sentenceTokenize(text);
  }

  /**
   * Erzeugt aus einer iterierbaren Collection von Strings einen String, der für ein OR Pattern geeignet ist
   *
   * @param list   die "Liste" an Wörtern
   * @param escape gibt an ob escapet werden soll
   * @param word   gibt an ob die wörter zwischen \b (Wörter) \b stehen soll
   * @return
   */
  public static String iterableToString(Iterable<String> list, boolean escape, boolean word) {
    StringBuilder stringBuilder = new StringBuilder();
    Iterator<String> iterator = list.iterator();

    if (word) {
      stringBuilder.append("\\b");
    }
    stringBuilder.append("(");
    if (iterator.hasNext()) {
      stringBuilder.append(escape ? Pattern.quote(iterator.next()) : iterator.next());
    }

    while (iterator.hasNext()) {
      stringBuilder.append("|");
      stringBuilder.append(escape ? Pattern.quote(iterator.next()) : iterator.next());
    }
    stringBuilder.append(")");
    if (word) {
      stringBuilder.append("\\b");
    }

    return stringBuilder.toString();
  }

  /**
   * Löscht die Umgebungsmetadaten und lässt den Inhalt übrig
   *
   * @param input
   * @return
   */
  public static String maskingEnvironment(String input) {
    return maskingEnvironment(input, new LinkedList<String>());
  }

  /**
   * Löscht die Umgebungsmetadaten und lässt den Inhalt übrig
   *
   * @param input
   * @param envs  Zusatzliste an Environments
   * @return
   */
  public static String maskingEnvironment(String input, List<String> envs) {
    StringBuilder stringBuilder = new StringBuilder(input);
    List<String> whiteList = Api.settings().getEnvironments();
    List<String> mathWhiteList = Api.settings().getMathEnvironments();
    boolean changed = false;

    // dies funktioniert nur, da zuerst alle ergebnisse vor dem ändern geholt werden
    // somit werden Verschachtelungen (auch wenn diese falsch erkannt werden) trotzdem richtig ausgewertet
    Matcher matcher = sEnvironment.matcher(input);

    Environment environment;
    // suchen aller umgebungen
    while (matcher.find()) {
      try {
        environment = Environment.getEnvironment(Command.getCommand(matcher.group(1), matcher.start(), input), input);
        // ist eine Math Umgebung
        if (mathWhiteList.contains(environment.getName())) {
          // damit auch alle Optionen von \begin{bla}[]{}{}{}{}{}{}{} mit genommen werden
          // lösche den Anfang und schreibe ein $
          stringBuilder.setCharAt(environment.getStart(), '$');
          for (int i = environment.getStart() + 1; i < environment.getContentStart(); i++) {
            stringBuilder.setCharAt(i, ' ');
          }
          // wenn ein Satzende in der Math Umgebung steht, muss dies nach dem $ kommen
          Matcher m = sSentenceEnd.matcher(environment.getContent());
          if (m.find()) {
            stringBuilder.setCharAt(environment.getContentStart() + m.start(1), '$');
            stringBuilder.setCharAt(environment.getContentStart() + m.end(1), m.group(1).charAt(0));
            // löscht das Ende
            for (int i = environment.getContentStart() + m.end(1) + 1; i <= environment.getEnd(); i++) {
              stringBuilder.setCharAt(i, ' ');
            }
          } else {
            stringBuilder.setCharAt(environment.getContentEnd() + 1, '$');
            // löscht das Ende
            for (int i = environment.getContentEnd() + 2; i <= environment.getEnd(); i++) {
              stringBuilder.setCharAt(i, ' ');
            }
          }
          // ist eine normale umgebung
        } else if (whiteList.contains(environment.getName()) || envs.contains(environment.getName())) {
          // damit auch alle Optionen von \begin{bla}[]{}{}{}{}{}{}{} mit genommen werden
          // lösche den anfang
          for (int i = environment.getStart(); i < environment.getContentStart(); i++) {
            stringBuilder.setCharAt(i, ' ');
          }
          // lösche das Ende
          for (int i = environment.getContentEnd() + 1; i <= environment.getEnd(); i++) {
            stringBuilder.setCharAt(i, ' ');
          }
          // ist keine gewollte Umgebung also lösche Anfang, Ende und Content
        } else {
          for (int i = environment.getStart(); i <= environment.getEnd(); i++) {
            stringBuilder.setCharAt(i, ' ');
          }
        }
        changed = true;
      } catch (LatexException e) {
        sLog.warning(e.getMessage());
      }
    }
    assert input.length() == stringBuilder.length();

    if (changed) {
      return maskingEnvironment(stringBuilder.toString(), envs);
    }
    return stringBuilder.toString();
  }

  /**
   * Wandelt Latex um
   *
   * @param input
   * @return
   */
  public static String maskingLatex(String input, boolean trans) {
    int length = input.length();
    boolean changed = false;

    if (trans) {
      // umwandeln von Latex in andere Zeichen
      Map<String, String> translate = Api.settings().getLatex();
      for (String latex : translate.keySet()) {
        if (latex.length() == translate.get(latex).length()) {
          input = input.replaceAll(String.format("(?<!\\\\)%s", Pattern.quote(latex)), Matcher.quoteReplacement(translate.get(latex)));
          assert input.length() == length;
        } else {
          sLog.warning(String.format("the lengths for the (%s) and the translation (%s) are not equal", latex, translate.get(latex)));
        }
      }
    }


    StringBuilder stringBuilder = new StringBuilder(input);
    Map<String, List<Integer>> whitelist = Api.settings().getWhiteList();
    String commands = iterableToString(whitelist.keySet(), true, false);
    Pattern pattern = Pattern.compile(String.format(sCommand, commands), Pattern.DOTALL);
    Matcher matcher = pattern.matcher(input);

    // erstellen des Ersatz Strings, der an die passende Stelle mit Leerzeichen davor eingesetzt wird

    while (matcher.find()) {
      try {
        Command command = Command.getCommand(matcher.group("Command"), matcher.start(), input);
        if (stringBuilder.charAt(command.getStart()) != '\\') {
          //String bla = stringBuilder.substring(command.getStart()).toString();
          //assert stringBuilder.charAt(command.getStart()) == '\\';
          return maskingLatex(stringBuilder.toString(), false);
        }

        StringBuilder insert = new StringBuilder();
        // wenn keine angabe dann nehme alle args
        if ((whitelist.get(command.getName()) == null) || whitelist.get(command.getName()).isEmpty()) {
          insert.append(command.getArgsString(' ')).append(' ');
          // nehme nur bestimmte args
        } else {
          for (int n : whitelist.get(command.getName())) {
            // von hinten zählen
            if (n <= 0) {
              if ((command.getArgs().size() + n) >= 0) {
                String arg = command.getArgs().get(command.getArgs().size() + n);
                insert.append(arg);
                insert.append(' ');
              } else {
                sLog.warning(String.format("command %s has no %d element", command, n));
              }
              // von vorn zählen
            } else {
              if (command.getArgs().size() > n) {
                String arg = command.getArgs().get(n);
                insert.append(arg);
                insert.append(' ');
              } else {
                sLog.warning(String.format("command %s has no %d element", command, n));
              }
            }
          }
        }
        // da das letzte Leerzeichen immer Gammel ist einfach löschen
        insert = new StringBuilder(insert.toString().trim());

        insert = insert.reverse();
        while (insert.length() <= command.getLength()) {
          insert.append(' ');
        }
        insert = insert.reverse();

        stringBuilder.replace(command.getStart(), command.getEnd() + 1, insert.toString());
        changed = true;
        //if (command.getLength() != insert.length()) {
        // ?! warum auch immer es um eins größer sein muss raff ich grad null
        //  assert command.getLength() == insert.length();
        //}
        assert command.getEnd() == (command.getStart() + command.getLength());
      } catch (LatexException e) {
        sLog.warning(e.getMessage());
      }
    }
    if (length != stringBuilder.length()) {
      assert length == stringBuilder.length();
    }
    if (changed) {
      return maskingLatex(stringBuilder.toString(), false);
    }
    return stringBuilder.toString();
  }

  /**
   * Löscht alle einzelnen \n
   *
   * @param input
   * @return
   */
  public static String noSingleLF(String input) {
    int length = input.length();
    input = input.replaceAll("(?<!\n {0,10000})\n(?! *\n)", " ");
    assert length == input.length() : "Laenge wurde veraendert! - noSingleLF: " + length + " #### " + input.length();
    return input;
  }

  /**
   * Löscht Latex
   *
   * @param input
   * @return
   */
  public static String removeLatex(String input) {
    Matcher matcher = sLatex.matcher(input);
    StringBuilder stringBuilder = new StringBuilder(input);
    Command command;
    int start = 0;
    while (matcher.find(start)) {
      try {
        command = Command.getCommand(matcher.group(1), matcher.start(), input);
        for (int i = command.getStart(); i <= command.getEnd(); i++) {
          stringBuilder.setCharAt(i, ' ');
        }
        start = command.getEnd();
      } catch (LatexException e) {
        sLog.warning(e.getMessage());
      }
    }
    assert input.length() == stringBuilder.length();
    return stringBuilder.toString();
  }

  /**
   * Löscht die Definitionsbefehle, damit diese nicht falsch erkannt werden. (da wir mit einer knf arbeiten)
   *
   * @param input
   * @return
   */
  public static String removeReNew(String input) {
    char[] array = input.toCharArray();

    Matcher matcher = sReNew.matcher(input);
    Command command;
    while (matcher.find()) {
      try {
        command = Command.getCommand(matcher.group(1), matcher.start(), input);
        for (int i = command.getStart(); i <= command.getEnd(); i++) {
          array[i] = ' ';
        }
      } catch (LatexException e) {
        sLog.warning(e.getMessage());
      }
    }

    return String.valueOf(array);
  }

  /**
   * Sortiert die \n richtig um die Absätze erkennen zu können
   *
   * @param input
   * @return
   */
  public static String sortNewline(String input) {
    input = input.replaceAll("\\\\\\\\", "\n\n");
    input = input.replaceAll("\\\\newline", "      \n\n");

    char[] array = input.toCharArray();

    Pattern pattern = Pattern.compile("\\s*?\\n(\\s*?\\n)+", Pattern.MULTILINE);
    Matcher matcher = pattern.matcher(input);

    while (matcher.find()) {
      int i = matcher.start();

      for (; i < (matcher.end() - 2); i++) {
        array[i] = ' ';
      }

      array[i] = '\n';
      array[i + 1] = '\n';
    }

    return String.valueOf(array);
  }

  /**
   * Sortiert alle überflüssigen Leerzeichen ans Satzende
   *
   * @param input   der Text
   * @param endings die Satzenden als String
   * @return
   */
  public static String sortWhitespaces(String input, String endings) {
    int length = input.length();

    StringBuilder stringBuilder = new StringBuilder(input.length());
    int maxIndex = input.length() - 1;
    int whitespaces = 0;
    int i = 0;

    // Gammel am Anfang ist egal
    while ((i < maxIndex) && ((input.charAt(i) == ' ') || (input.charAt(i) == '\n'))) {
      stringBuilder.append(input.charAt(i));
      i++;
    }

    while (i <= maxIndex) {
      // die zu vielen Leerzeichen zählen und überspringen
      if ((input.charAt(i) == ' ') || (input.charAt(i) == '\n')) {
        stringBuilder.append(input.charAt(i));
        i++;
        while ((i < maxIndex) && (input.charAt(i) == ' ')) {
          whitespaces++;
          i++;
        }
        continue;
      }

      // Satzende gefunden
      if (endings.contains(String.valueOf(input.charAt(i))) && ((i < maxIndex) && !Character.isDigit(input.charAt(i + 1)))) {
        if ((stringBuilder.length() > 0) && (stringBuilder.charAt(stringBuilder.length() - 1) == ' ')) {
          whitespaces++;
          stringBuilder.deleteCharAt(stringBuilder.length() - 1);
          stringBuilder.append(input.charAt(i));
        } else {
          stringBuilder.append(input.charAt(i));
        }
        i++;
        while (whitespaces > 0) {
          stringBuilder.append(' ');
          whitespaces--;
        }
        while ((i < maxIndex) && (input.charAt(i) == ' ')) {
          stringBuilder.append(input.charAt(i));
          i++;
        }
        continue;
      }
      stringBuilder.append(input.charAt(i));
      i++;
    }
    while (whitespaces > 0) {
      stringBuilder.append(' ');
      whitespaces--;
    }

    assert length == stringBuilder.length() : "Laenge wurde veraendert! - sortWhitespaces: " + length + " #### " + stringBuilder.length();
    return stringBuilder.toString();
  }
}
