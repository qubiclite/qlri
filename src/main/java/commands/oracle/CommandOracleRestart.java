package commands.oracle;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.general.ResponseSuccess;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import oracle.OracleManager;
import oracle.OracleWriter;

import java.util.ArrayList;
import java.util.Map;

public class CommandOracleRestart extends Command {

    public static final CommandOracleRestart instance = new CommandOracleRestart();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("oracle handle").setExampleValue("JR").setDescription("restarts the oracle that starts with this tryte sequence"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "oracle_restart";
    }

    @Override
    public String getAlias() {
        return "or";
    }

    @Override
    public String getDescription() {
        return "restarts an oracle that was paused with '"+ CommandOraclePause.instance.getName() +"', makes it process its qubic again";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("oracle restarted");
    }


    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        String oracleHandle = (String)parMap.get("oracle_handle");
        ArrayList<OracleWriter> ows = persistence.findAllOracleWritersWithHandle(oracleHandle);

        if(ows.size() != 1)
            return new ResponseError("there are "+ows.size()+" oracles with the handle '"+oracleHandle+"'");

        ows.get(0).getManager().start();
        //new OracleManager(ows.get(0)).start();
        return new ResponseSuccess();
    }
}
