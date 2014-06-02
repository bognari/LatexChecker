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

/**
 * Created by Jochen on 12.03.14.
 * Methode zur Überprüfung des Punktes 1: Jede Grafik ist min. 1 mal referenziert
 * testet, ob jede Umgebung (die angegeben ist) ein Label hat, und ob jedes im Dokument existierende label auch
 * referenziert wird.
 */
public class LabelCheck extends Module {

  //private static final Pattern sPatterLabel = Pattern.compile("(?<!\\\\)\\\\label\\s*\\{(.*?)\\}");
  private final Map<String, Position> mLabels = new HashMap<>();
  /**
   * "Environments" = ["Umgebung"] <p></p>
   * Liste zu prüfenden Umgebungen (für Regex setzte UseRegex auf true)
   * <p></p> default: empty
   */
  @Expose
  @SerializedName("Environments")
  private List<String> cEnvironments = new LinkedList<>();
  /**
   * "UseRegex" = true / false <p></p>
   * Gibt an, ob Regex in Listen benutzt wird
   * <p></p> default: false
   */
  @Expose
  @SerializedName("UseRegex")
  private boolean cUseRegex = false;

  @Override
  public void runModule() {
    noLabels();
    labels();
    refs();
    keinRef();
  }

  private void noLabels() {
    for (Environment env : Api.getEnvironments(Misc.iterableToString(cEnvironments, !cUseRegex, !cUseRegex))) {
      //Matcher matcher;
      //matcher = sPatterLabel.matcher(env.getContent());
      List<Command> labels = Command.getCommands("label\\b", env.getContent());
      if (labels.isEmpty()) {
        mLog.info(new Result(mName, env.getPosition(), String.format("No label")).toString());
        //mLog.warning("Kein label in der Datein "+env.getPosition().getFile() +" and der Position "+ env.getPosition().getLine() +" vorhanden");
      }
      /*matcher.reset();
      while (matcher.find()) {
        if (mLabels.containsKey(matcher.group(1))) {
          mLog.info(new Result(mName, env.getPosition(), String.format("There is already a label %s", matcher.group(1))).toString());
          //mLog.warning("Es exisitiert bereits eine label mit dem gleichen Namen in der Datei!");
        }
        mLabels.put(matcher.group(1), env.getPosition());
      }*/
    }
  }

  private void labels() {
    List<Command> labels = Api.getCommands("label\\b");
    /*Matcher matcher;
    matcher = sPatterLabel.matcher(Api.getRawContent());
    while (matcher.find()) {
      if (mLabels.containsKey(matcher.group(1))) {
        mLog.info(new Result(mName, Api.getPosition(matcher.start()), String.format("There is already a label %s", matcher.group(1))).toString());
        //mLog.warning("Es exisitiert bereits eine label mit dem gleichen Namen in der Datei!");
      }
      mLabels.put(matcher.group(1), Api.getPosition(matcher.start()));
    }*/
    for (Command label : labels) {
      if (mLabels.containsKey(label.getArgs().get(0))) {
        mLog.info(new Result(mName, label.getPosition(), String.format("There is already a label %s", label.getArgs().get(0))).toString());
        //mLog.warning("Es exisitiert bereits eine label mit dem gleichen Namen in der Datei!");
      }
      mLabels.put(label.getArgs().get(0), label.getPosition());
    }
  }

  private void refs() {
    List<Command> commands = Api.getCommands("ref\\b");
    for (Command command : commands) {
      mLabels.remove(command.getArgs().get(0));
    }
  }

  private void keinRef() {
    for (String label : mLabels.keySet()) {
      mLog.info(new Result(mName, mLabels.get(label), String.format("There is no ref. for the label %s", label)).toString());
      //mLog.warning("Für das Label " + label + " in der Datein " + mLabels.get(label).getFile() + " in der Zeile " + mLabels.get(label).getLine() + " ist keine Refernenz vorhanden");
    }
  }
}
