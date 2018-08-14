package commands.oracle;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseSuccess;
import api.resp.oracle.ResponseOracleList;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import oracle.OracleWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import tangle.TryteTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandOracleList extends CommandOracleAbstract {

    public static final CommandOracleList instance = new CommandOracleList();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{ });

    private static final CallValidator CV_TERMINAL = new CallValidator(new ParameterValidator[]{
            new TryteValidator(0, 81).setName("filter").setDescription("filters the list and only shows the oracles starting with this sequence").makeOptional("")
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
        return "oracle_list";
    }

    @Override
    public String getAlias() {
        return "ol";
    }

    @Override
    public String getDescription() {
        return "Lists all oracles stored in the persistence";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        JSONArray oracles = ((ResponseOracleList)response).getOracles();

        println("found " + oracles.length() + " oracle(s):");
        for(int i = 0; i < oracles.length(); i++) {
            JSONObject oracleObject = oracles.getJSONObject(i);
            String id = oracleObject.getString("id");
            String state = oracleObject.getString("state");
            String qubic = oracleObject.getString("qubic");
            println("   > "+id+" (QUBIC: "+qubic+", STATE: "+state+")");
        }
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String filter = (String)parMap.get("filter");
        List<OracleWriter> ows = persistence.findAllOracleWritersWithHandle(filter != null ? filter : "");
        JSONArray arr = new JSONArray();
        for(OracleWriter ow : ows) {
            JSONObject oracleObject = new JSONObject();
            oracleObject.put("id", ow.getID());
            oracleObject.put("qubic", ow.getQubicReader().getID());
            oracleObject.put("state", ow.getManager().getState());
            arr.put(oracleObject);
        }
        return new ResponseOracleList(arr);
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {
        JSONArray arr = new JSONArray();
        arr.put(TryteTool.generateRandom(81));
        arr.put(TryteTool.generateRandom(81));
        return new ResponseOracleList(arr);
    }
}
