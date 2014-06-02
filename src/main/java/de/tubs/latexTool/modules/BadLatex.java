package de.tubs.latexTool.modules;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import de.tubs.latexTool.core.Api;
import de.tubs.latexTool.core.entrys.Command;
import de.tubs.latexTool.core.entrys.Environment;
import de.tubs.latexTool.core.entrys.Result;
import de.tubs.latexTool.core.util.Misc;

import java.util.IllegalFormatException;
import java.util.LinkedList;
import java.util.List;

/**
 * Dieses Modul findet "verbotene" Befehle und Umgebungen
 */
public class BadLatex extends Module {

  private static final String sMsgCommand = "Bad command: %s";
  /**
   * "MsgCommand" = "Text" <p></p>
   * Der Nachrichten Prototyp im Falle verbotener Befehle
   * <p></p> default: "Bad command: %s"
   */
  @Expose
  @SerializedName("MsgCommand")
  private String cMsgCommand = sMsgCommand;
  private static final String sMsgEnvironments = "Bad environment: %s";
  /**
   * "MsgEnvironments" = "Text" <p></p>
   * Der Nachrichten Prototyp im Falle verbotener Umgebungen
   * <p></p> default: "Bad environment: %s"
   */
  @Expose
  @SerializedName("MsgEnvironment")
  private String cMsgEnvironment = sMsgEnvironments;
  /**
   * "Commands" = ["Befehl"] <p></p>
   * Liste der verbotenen Befehle (für Regex setzte UseRegex auf true)
   * <p></p> default: empty
   */
  @Expose
  @SerializedName("Commands")
  private List<String> cCommands = new LinkedList<>();
  /**
   * "Environments" = ["Umgebung"] <p></p>
   * Liste der verbotenen Umgebungen (für Regex setzte UseRegex auf true)
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
  protected void validation() {
    try {
      String.format(cMsgCommand, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(BadLatex.class.getName(), mName, e);
      cMsgCommand = sMsgCommand;
    }
    try {
      String.format(cMsgEnvironment, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(BadLatex.class.getName(), mName, e);
      cMsgEnvironment = sMsgEnvironments;
    }
  }

  @Override
  public void runModule() {
    if (!cCommands.isEmpty()) {
      mLog.fine("start bad command checking");
      List<Command> commands = Api.getCommands(Misc.iterableToString(cCommands, !cUseRegex, !cUseRegex));

      for (Command command : commands) {
        mLog.info(new Result(mName, command.getPosition(), String.format(cMsgCommand, command.getName())).toString());
      }
      mLog.fine("finish bad command checking");
    }

    if (!cEnvironments.isEmpty()) {
      mLog.fine("start bad environment checking");
      List<Environment> environments = Api.getEnvironments(Misc.iterableToString(cEnvironments, !cUseRegex, !cUseRegex));

      for (Environment environment : environments) {
        mLog.info(new Result(mName, environment.getPosition(), String.format(cMsgEnvironment, environment.getName())).toString());
      }
      mLog.fine("finish bad environment checking");
    }
  }
}
