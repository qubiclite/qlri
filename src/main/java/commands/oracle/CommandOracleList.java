package commands.oracle;

import api.resp.general.ResponseAbstract;
import api.resp.oracle.ResponseOracleList;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import oracle.OracleWriter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class CommandOracleList extends Command {

    public static final CommandOracleList instance = new CommandOracleList();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(1, 81).setName("oracle id filter").setExampleValue("JR").setDescription("filters the list and only shows the oracles starting with this sequence").makeOptional("")
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
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
        return "prints the full list of all oracles stored in the persistence";
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
        String oracleIDFilter = (String)parMap.get("oracle_id_filter");
        ArrayList<OracleWriter> ows = persistence.findAllOracleWritersWithHandle(oracleIDFilter);
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
}
