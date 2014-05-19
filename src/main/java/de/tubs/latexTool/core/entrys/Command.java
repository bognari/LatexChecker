package de.tubs.latexTool.core.entrys;

import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.LatexException;
import de.tubs.latexTool.core.util.Misc;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse stellt einen Latex Befehl dar
 */
public class Command implements ILatex, IPosition {
  private static final Pattern sPOpen = Pattern.compile("^(\\s*(?<Args>(?<!\\\\)\\{)|(?<Opt>\\[)|(?<Plain>\\||!))");
  /**
   * Liste der Argumente / Optionen
   */
  private final List<String> mArgs;
  /**
   * Länge in Zeichen
   */
  private final int mLength;
  /**
   * Name des Befehls
   */
  private final String mName;
  /**
   * Start des Befehlt als Abstand in Zeichen
   */
  private final int mStart;

  /**
   * Erstellt einen neuen Befehl
   *
   * @param name  der Name
   * @param start die Position als Abstand in Zeichen vom Dokumenten Anfang
   * @param args  die Argumente / Optionen
   */
  private Command(final String name, final int start, final int length, final List<String> args) {
    mName = name.trim();
    mStart = start;
    mLength = length;

    // prüfen ob es ein befehl ist der maskierte argumente besitzt
    // wenn ja demaskieren
    if (Api.settings().getBadCom().contains(name)) {
      List<String> newArgs = new LinkedList<>();
      for (String arg : args) {
        //newArgs.add(arg.replaceAll(Pattern.quote("\\{"), "{").replaceAll(Pattern.quote("\\}"), "}").replaceAll(Pattern.quote("\\["), "[").replaceAll(Pattern.quote("\\]"), "]").replaceAll(Pattern.quote("\\%"), "%").replaceAll(Pattern.quote("\\\\"), "\\\\"));
        newArgs.add(arg.replaceAll("(\\\\(\\{|\\}|%|\\\\))", "$1"));
      }
      mArgs = Collections.unmodifiableList(newArgs);
    } else {
      mArgs = Collections.unmodifiableList(args);
    }
  }

  private Command(final String command, final int start) {
    mName = command;
    mStart = start;
    mLength = command.length() + 1;
    mArgs = Collections.unmodifiableList(new LinkedList<String>());
  }


  /**
   * Ist eine Stackmaschine, um Latex Befehle richtig zu lesen (mit Argumente usw)
   * <p/>
   * Achtung: Ausdrücke bei denen die Klammern um das Argument weggelassen werden kann, werden nicht erkannt!
   *
   * @param start   gibt den Start des befehls an
   * @param command der Befehl
   * @param input   der Text
   * @return
   */
  public static Command getCommand(final String command, final int start, final String input) throws LatexException {
    int end = start + command.length() + 1;
    List<String> args = new LinkedList<>();
    String content;
    Pattern pClose;
    Matcher open;
    Matcher close;
    boolean find;

    do {
      content = input.substring(end);
      open = sPOpen.matcher(content);

      // testen ob der befehl überhaupt irgendwelche argumente besitzt
      // ja das "=" ist absicht
      if (find = open.find()) {
        switch (open.group()) {
          case "|":  // im folgenden behandeln
          case "!":
            assert open.group().equals("|") || open.group().equals("!");
            pClose = Pattern.compile(String.format("\\Q%1$s\\E(?<Content>.*?)\\Q%1$s\\E", open.group()));
            close = pClose.matcher(content);
            if (close.find()) {
              args.add(close.group("Content").trim());
              end += close.end();
            } else {
              throw new LatexException(new Command(command, start), String.format("no closing bracket (%s)", open.group()));
            }
            break;
          case "[":
            assert open.group().equals("[");
            // ab hier brauchen wir eine Stackmaschine
            end += readStack(command, open.end(), "[", "]", content, args);
            break;
          default:  // kann nur der \\s*{ fall sein
            assert open.group().contains("{");
            end += readStack(command, open.end(), "{", "}", content, args);
        }
      }
    } while (find);
    return new Command(command, start, end - 1 - start, args);
  }

  /**
   * Diese Methode List ein Argument Mittels einer Stackmaschine ein
   *
   * @param command  der Name des Befehls
   * @param start    die Startposition der Anfangs Klammer
   * @param oBracket öffnende Klammer
   * @param cBracket schließende Klammer
   * @param content  der ganze Inhalt
   * @param args     die Liste der Argumente, hier wird das Argument hinzugefügt
   * @return gibt die neue Position an, ab der auf weitere Args getestet werden kann.
   * @throws LatexException falls eine Klammer fehlt
   */
  private static int readStack(String command, int start, String oBracket, String cBracket, String content, final List<String> args) throws LatexException {
    Deque<String> stack = new LinkedList<>();
    Pattern pClose = Pattern.compile(String.format("(?<!\\\\)(?<Open>\\%s)|(?<Close>\\%s)", oBracket, cBracket));
    Matcher close = pClose.matcher(content);
    do {
      if (close.find()) {
        if (close.group().equals(oBracket)) {
          stack.add(close.group());
        } else {
          stack.poll();
          if (stack.isEmpty()) {
            args.add(content.substring(start, close.start()).trim());
            return close.end();
          }
        }
      } else {
        throw new LatexException(new Command(command, start), String.format("no closing bracket (%s)", cBracket));
      }
    } while (true);
  }

  /**
   * Gibt eine Liste der Argumenten / Optionen zurück
   *
   * @return
   */
  public List<String> getArgs() {
    return mArgs;
  }

  /**
   * Gibt die Args als String Liste zurück
   *
   * @param delimiter
   * @return
   */
  public String getArgsString(char delimiter) {
    StringBuilder stringBuilder = new StringBuilder();

    Iterator<String> iterator = mArgs.iterator();

    if (iterator.hasNext()) {
      stringBuilder.append(iterator.next());
    }

    while (iterator.hasNext()) {
      stringBuilder.append(delimiter);
      stringBuilder.append(iterator.next());
    }

    return stringBuilder.toString();
  }

  /**
   * Gibt die Länge in Zeichen des ganzen Befehls
   *
   * @return
   */
  public int getLength() {
    return mLength;
  }

  /**
   * Gibt den Namen zurück
   *
   * @return
   */
  public String getName() {
    return mName;
  }

  @Override
  public int hashCode() {
    int result = mArgs.hashCode();
    result = 31 * result + mLength;
    result = 31 * result + mName.hashCode();
    result = 31 * result + mStart;
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Command command = (Command) o;

    if (mLength != command.mLength) return false;
    if (mStart != command.mStart) return false;
    if (!mArgs.equals(command.mArgs)) return false;
    return mName.equals(command.mName);

  }

  @Override
  public String toString() {
    return mName + " " + Misc.iterableToString(mArgs, false) + (getPosition() == null ? "" : " " + getPosition());
  }

  @Override
  public Position getEndPosition() {
    try {
      return Api.getPosition(getEnd());
    } catch (NullPointerException e) {
      return null;
    }
  }


  /**
   * Gibt das Ende (Zeichenanzahl) des Befehls an
   *
   * @return
   */
  public int getEnd() {
    return mStart + mLength;
  }


  /**
   * Gibt die Position zurück
   *
   * @return
   */
  public Position getPosition() {
    try {
      return Api.getPosition(mStart);
    } catch (NullPointerException e) {
      return null;
    }
  }

  /**
   * gibt den Start zurück
   *
   * @return
   */
  public int getStart() {
    return mStart;
  }


}
