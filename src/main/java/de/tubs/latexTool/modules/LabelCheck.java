package de.tubs.latexTool.modules;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.Command;
import de.tubs.latexTool.core.entrys.Environment;
import de.tubs.latexTool.core.entrys.Position;
import de.tubs.latexTool.core.entrys.Result;
import de.tubs.latexTool.core.util.Misc;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Jochen on 12.03.14.
 * Methode zur Überprüfung des Punktes 1: Jede Grafik ist min. 1 mal referenziert
 */
public class LabelCheck extends Module {

  private static final Pattern sPatterLabel = Pattern.compile("(?<!\\\\)\\\\label\\{(.*?)\\}");
  private final Map<String, Position> mLabels = new HashMap<>();
  @Expose
  @SerializedName("EnvList")
  private List<String> mEnvList = new LinkedList<>();

  /**
   * When an object implementing interface <code>Runnable</code> is used
   * to create a thread, starting the thread causes the object's
   * <code>run</code> method to be called in that separately executing
   * thread.
   * <p/>
   * The general contract of the method <code>run</code> is that it may
   * take any action whatsoever.
   *
   * @see Thread#run()
   */
  @Override
  public void run() {
    labels();
    refs();
    keinRef();
  }

  private void labels() {
    for (Environment env : Api.getEnvironments(Misc.iterableToString(mEnvList, true))) {
      Matcher matcher;
      matcher = sPatterLabel.matcher(env.getContent());
      if (!matcher.find()) {
        mLog.info(new Result(mName, env.getPosition(), String.format("No label")).toString());
        //mLog.warning("Kein label in der Datein "+env.getPosition().getmFile() +" and der Position "+ env.getPosition().getLine() +" vorhanden");
      }
      matcher.reset();
      while (matcher.find()) {
        if (mLabels.containsKey(matcher.group(1))) {
          mLog.info(new Result(mName, env.getPosition(), String.format("There is already a label %s", matcher.group(1))).toString());
          //mLog.warning("Es exisitiert bereits eine label mit dem gleichen Namen in der Datei!");
        }
        mLabels.put(matcher.group(1), env.getPosition());
      }
    }
  }

  private void refs() {
    List<Command> commands = Api.getCommands("\\bref\\b");
    for (Command command : commands) {
      mLabels.remove(command.getArgs().get(0));
    }
  }

  private void keinRef() {
    for (String label : mLabels.keySet()) {
      mLog.info(new Result(mName, mLabels.get(label), String.format("There is no ref. for the label %s", label)).toString());
      //mLog.warning("Für das Label " + label + " in der Datein " + mLabels.get(label).getmFile() + " in der Zeile " + mLabels.get(label).getLine() + " ist keine Refernenz vorhanden");
    }
  }
}
