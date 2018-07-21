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

import java.util.Map;

public class CommandOracleDelete extends Command {

    public static final CommandOracleDelete instance = new CommandOracleDelete();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(1, 81).setName("oracle handle").setExampleValue("JR").setDescription("deletes the oracle that starts with this tryte sequence"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "oracle_delete";
    }

    @Override
    public String getAlias() {
        return "od";
    }

    @Override
    public String getDescription() {
        return "removes an oracle from the persistence (oracle's private key will be deleted: cannot be undone)";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("oracle deleted from persistence");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String oracleID = (String)parMap.get("oracle_handle");
        OracleWriter ow = persistence.findOracleWriterByHandle(oracleID);

        if(ow == null)
            return new ResponseError("you do not own a oracle with the id '"+oracleID+"'");

        persistence.deleteOracleWriter(ow);
        return new ResponseSuccess();
    }
}
