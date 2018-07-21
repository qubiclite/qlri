package commands.oracle;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.general.ResponseSuccess;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import oracle.OracleWriter;

import java.util.ArrayList;
import java.util.Map;

public class CommandOraclePause extends Command {

    public static final CommandOraclePause instance = new CommandOraclePause();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(1, 81).setName("oracle handle").setExampleValue("JR").setDescription("pauses the oracle that starts with this tryte sequence"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "oracle_pause";
    }

    @Override
    public String getAlias() {
        return "op";
    }

    @Override
    public String getDescription() {
        return "temporarily stops an oracle from processing its qubic, can be undone with '" + CommandOracleRestart.instance.getName() + "'";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("pausing oracle after epoch ends");
    }


    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        String oracleHandle = (String)parMap.get("oracle_handle");
        ArrayList<OracleWriter> ows = persistence.findAllOracleWritersWithHandle(oracleHandle);

        if(ows.size() != 1)
            return new ResponseError("there are "+ows.size()+" oracles with the handle '"+oracleHandle+"'");

        ows.get(0).getManager().terminate();
        return new ResponseSuccess();
    }
}
