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
 * Dieses Modul findet "verbotene" Befehle <p></p>
 * #############<br></br>>
 * MsgCommand<br></br>
 * MsgEnvironments<br></br>
 * Commands<br></br>
 * Environments<br></br>
 * #############<br></br>
 */
public class BadLatex extends Module {

  @Expose
  @SerializedName("Commands")
  private List<String> mCommands = new LinkedList<>();
  @Expose
  @SerializedName("Environments")
  private List<String> mEnvironments = new LinkedList<>();
  @Expose
  @SerializedName("MsgCommand")
  private String mMsgCommand = "Bad command: %s";
  @Expose
  @SerializedName("MsgEnvironments")
  private String mMsgEnvironments = "Bad environment: %s";

  @Override
  public void run() {
    mLog.fine(String.format("%s start", mName));

    if (!mCommands.isEmpty()) {
      mLog.fine("start bad command checking");
      List<Command> commands = Api.getCommands(Misc.iterableToString(mCommands, true));

      for (Command command : commands) {
        mLog.info(new Result(mName, command.getPosition(), String.format(mMsgCommand, command.getName())).toString());
      }
      mLog.fine("finish bad command checking");
    }

    if (!mEnvironments.isEmpty()) {
      mLog.fine("start bad environment checking");
      List<Environment> environments = Api.getEnvironments(Misc.iterableToString(mEnvironments, true));

      for (Environment environment : environments) {
        mLog.info(new Result(mName, environment.getPosition(), String.format(mMsgEnvironments, environment.getName())).toString());
      }
      mLog.fine("finish bad environment checking");
    }

    mLog.fine(String.format("%s finish", mName));
  }

  @Override
  protected void validation() {
    try {
      String.format(mMsgCommand, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(BadLatex.class.getName(), mName, e);
      mMsgCommand = "Bad command: %s";
    }
    try {
      String.format(mMsgEnvironments, "test");
    } catch (IllegalFormatException e) {
      mLog.throwing(BadLatex.class.getName(), mName, e);
      mMsgEnvironments = "Bad environment: %s";
    }
  }
}
