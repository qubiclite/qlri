package commands.oracle;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.general.ResponseSuccess;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import oracle.OracleWriter;

import java.util.List;
import java.util.Map;

public class CommandOracleRestart extends CommandOracleAbstract {

    public static final CommandOracleRestart instance = new CommandOracleRestart();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(81, 81).setName("id").setDescription("oracle ID"),
    });

    private static final CallValidator CV_TERMINAL = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("id").setExampleValue("JR").setDescription("restarts the oracle that starts with this tryte sequence"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public CallValidator getCallValidatorForTerminal() {
        return CV_TERMINAL;
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
        return "Restarts an oracle that was paused with '"+ CommandOraclePause.instance.getName() +"', makes it process its qubic again.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("oracle restarted");
    }


    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        String oracleHandle = (String)parMap.get("oracle_handle");
        List<OracleWriter> ows = persistence.findAllOracleWritersWithHandle(oracleHandle);

        if(ows.size() != 1)
            return new ResponseError("there are "+ows.size()+" oracles with the handle '"+oracleHandle+"'");

        ows.get(0).getManager().start();
        //new OracleManager(ows.get(0)).start();
        return new ResponseSuccess();
    }
}
