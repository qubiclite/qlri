package commands;

import resp.general.ResponseAbstract;
import resp.general.ResponseError;
import resp.general.ResponseSuccess;
import commands.app.CommandAppInstall;
import commands.app.CommandAppList;
import commands.app.CommandAppUninstall;
import commands.iam.*;
import commands.oracle.*;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.qubic.*;
import main.Main;
import main.Persistence;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.Map;
import java.util.StringJoiner;


public abstract class Command {

    // a list of all COMMANDS. new COMMANDS have to be added to this list to become callable.
    public static final Command[] COMMANDS = {

            CommandHelp.instance,
            CommandChangeNode.instance,
            CommandFetchEpoch.instance,
            CommandExport.instance,
            CommandImport.instance,

            CommandQubicRead.instance,
            CommandQubicList.instance,
            CommandQubicCreate.instance,
            CommandQubicDelete.instance,
            CommandQubicListApplications.instance,
            CommandQubicAssemblyAdd.instance,
            CommandQubicAssemble.instance,
            CommandQubicTest.instance,
            CommandQubicQuickRun.instance,
            CommandQubicConsensus.instance,

            CommandOracleCreate.instance,
            CommandOracleDelete.instance,
            CommandOracleList.instance,
            CommandOraclePause.instance,
            CommandOracleRestart.instance,

            CommandIAMCreate.instance,
            CommandIAMDelete.instance,
            CommandIAMList.instance,
            CommandIAMWrite.instance,
            CommandIAMRead.instance,

            CommandAppList.instance,
            CommandAppInstall.instance,
            CommandAppUninstall.instance,
    };

    /**
     * Finds an command by name or alias.
     * @param commandString the name or alias of the command searched
     * @return the found command, NULL if no action found
     * */
    public static Command findCommand(String commandString) {
        for(Command c : COMMANDS)
            if(c.getName().equals(commandString) || c.getAlias().equals(commandString))
                return c;
        return null;
    }

    /**
     * @return the name of the command (which can be used to call the command).
     * */
    public abstract String getName();

    /**
     * @return the alias of the command (which can be used to call the command).
     * */
    public abstract String getAlias();

    /**
     * @return a short description, describing what the command does.
     * */
    public abstract String getDescription();

    /**
     * @return the CallValidator object used to validate whether the parameters of a call are correct.
     * */
    public abstract CallValidator getCallValidator();

    /**
     * @return the CallValidator object used to validate whether the parameters of a call are correct if called from terminal.
     * */
    public CallValidator getCallValidatorForTerminal() {
        return getCallValidator();
    }

    /**
     * Performs the command.
     * @param persistence the Persistence object as access point for oracles, qubics etc.
     * @param par         the parameters passed with the command call.
     * */
    public abstract void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par);

    public abstract ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap);

    public ResponseAbstract terminalWrappedPerform(Persistence persistence, String[] par) {
        Map<String, Object> parMap = getCallValidator().genParMap(par);
        ResponseAbstract response = perform(persistence, parMap);

        if(response instanceof ResponseError)
            println("ERROR: " + ((ResponseError) response).getError());

        return response;
    }

    public ResponseSuccess getSuccessResponseExample() {
        return null;
    }

    public String getGroup() {
        return "General";
    }

    /**
     * @return TRUE if command can also be accessed through API, FALSE if command can be accessed only through terminal
     * */
    public boolean isRemotelyAvailable() {
        return true;
    }

    protected static void println(String s) {
        Main.println(s);
    }
}
