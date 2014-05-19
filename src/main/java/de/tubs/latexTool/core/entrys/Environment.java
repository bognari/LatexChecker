package de.tubs.latexTool.core.entrys;

import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.LatexException;
import de.tubs.latexTool.core.util.Misc;

import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Diese Klasse stellt einen Environment Eintrag dar, z.B. \begin{environment}content\end{environment}
 */
public class Environment implements ILatex, IPosition {
  /**
   * Pattern für \item
   */
  private static final Pattern sItem = Pattern.compile("(?<!\\\\)\\\\(?<Item>item)(\\[.*?\\])?(<.*?>)?\\s*?(?<Content>.*?)\\s*?(?=\\\\item)?", Pattern.DOTALL);

  /**
   * Der Befehl mit dem die Umgebung erstellt wurde
   */
  private final Command mCommand;
  /**
   * Der Inhalt
   */
  private final String mContent;
  /**
   * Ende vom \end
   */
  private final int mEnd;
  /**
   * Ende vom content
   */
  private final int mEndContent;

  private Environment(Command command, String content, int end) {
    mCommand = command;
    mEnd = end;
    mEndContent = command.getEnd() + content.length();

    if (Api.settings().getBadEnv().contains(mCommand.getName())) {
      //mContent = content.replaceAll(Pattern.quote("\\{"), "{").replaceAll(Pattern.quote("\\}"), "}").replaceAll(Pattern.quote("\\%"), "%").replaceAll(Pattern.quote("\\\\"), "\\\\");
      mContent = content.replaceAll("(\\\\(\\{|\\}|%|\\\\))", "$1");
    } else {
      mContent = content;
    }
  }

  /**
   * Liest ein Environment mittels stack maschine ein
   *
   * @param command
   * @param content
   * @return
   */
  public static Environment getEnvironment(Command command, String content) throws LatexException {
    if (command.getArgs().isEmpty() || !command.getName().equalsIgnoreCase("begin")) {
      throw new IllegalArgumentException("command is not a environment begin");
    }

    String environment = command.getArgs().get(0);
    Pattern pattern = Pattern.compile(String.format("(?<!\\\\)\\\\((begin)|(end))\\s*\\{(%s)\\}", Pattern.quote(environment)));
    Matcher matcher = pattern.matcher(content);
    Deque<Command> stack = new LinkedList<>();

    stack.push(command);
    Command nextCommand = command;

    while (!stack.isEmpty() && matcher.find(nextCommand.getEnd() - 1)) {
      switch (matcher.group(1)) {
        case "begin":
          nextCommand = Command.getCommand("begin", matcher.start(), content);
          stack.push(nextCommand);
          break;
        case "end":
          nextCommand = Command.getCommand("end", matcher.start(), content);
          stack.pop();
          break;
      }
    }

    if (!stack.isEmpty()) {
      throw new LatexException(new Environment(command, "", command.getEnd()));
    }

    return new Environment(command, content.substring(command.getEnd() + 1, nextCommand.getStart() - 1), nextCommand.getEnd());
  }

  public List<String> getArgs() {
    List<String> tmp = new LinkedList<>(mCommand.getArgs());
    tmp.remove(0);
    return Collections.unmodifiableList(tmp);
  }

  public Command getCommand() {
    return mCommand;
  }

  public String getContent() {
    return mContent;
  }

  public Position getContentEndPosition() {
    try {
      return Api.getPosition(getContentEnd());
    } catch (NullPointerException e) {
      return null;
    }
  }

  public int getContentEnd() {
    return mEndContent;
  }

  public Position getContentStartPosition() {
    try {
      return Api.getPosition(getContentStart());
    } catch (NullPointerException e) {
      return null;
    }
  }

  public int getContentStart() {
    return mCommand.getEnd() + 1;
  }

  public int getEnd() {
    return mEnd;
  }

  @Override
  public Position getEndPosition() {
    try {
      return Api.getPosition(mEnd);
    } catch (NullPointerException e) {
      return null;
    }
  }

  public Position getPosition() {
    return mCommand.getPosition();
  }

  public int getStart() {
    return mCommand.getStart();
  }

  /**
   * Gibt eine Liste aller \item zurück
   *
   * @return
   */
  public List<Text> getItems() {
    String content = mContent;
    content = Misc.maskingLatex(content, true);
    content = Misc.maskingEnvironment(content, Api.settings().getItemEnvironments());
    List<Text> list = new LinkedList<>();
    Matcher matcher = sItem.matcher(content);
    while (matcher.find()) {
      list.add(new Text(matcher.group("Content"), matcher.start(4), getContentStart()));
    }
    return list;
  }

  @Override
  public int hashCode() {
    int result = mContent.hashCode();
    result = 31 * result + mCommand.hashCode();
    return result;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    final Environment that = (Environment) o;

    if (mEnd != that.mEnd) return false;
    if (mEndContent != that.mEndContent) return false;
    if (!mContent.equals(that.mContent)) return false;
    return mCommand.equals(that.mCommand);
  }

  @Override
  public String toString() {
    return String.format("%s %s", getName(), getPosition());
  }

  public String getName() {
    return mCommand.getArgs().get(0);
  }
}
