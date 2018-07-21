package commands.qubic;

import api.resp.general.ResponseAbstract;
import api.resp.qubic.ResponseQubicList;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import org.json.JSONArray;
import org.json.JSONObject;
import qubic.QubicWriter;

import java.util.ArrayList;
import java.util.Map;

public class CommandQubicList extends Command {

    public static final CommandQubicList instance = new CommandQubicList();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(1, 81).setName("qubic id filter").setExampleValue("G9").setDescription("filters the list and only shows the qubics starting with this sequence").makeOptional("")
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "qubic_list";
    }

    @Override
    public String getAlias() {
        return "ql";
    }

    @Override
    public String getDescription() {
        return "prints the full list of all qubics stored in the persistence";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        JSONArray qubics = ((ResponseQubicList)response).getQubics();

        println("found " + qubics.length() + " qubic(s):");
        for(int i = 0; i < qubics.length(); i++) {
            JSONObject qubicObject = qubics.getJSONObject(i);
            String id = qubicObject.getString("id");
            String state = qubicObject.getString("state");
            println("   > " + id + " (STATE: "+state+")");
        }
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String handle = (String)parMap.get("qubic_id_filter");
        ArrayList<QubicWriter> qws = persistence.findAllQubicWritersWithHandle(handle);
        JSONArray arr = new JSONArray();

        for(QubicWriter qw : qws) {
            JSONObject oracleObject = new JSONObject();
            oracleObject.put("id", qw.getID());
            oracleObject.put("state", qw.getState());
            arr.put(oracleObject);
        }

        return new ResponseQubicList(arr);
    }
}
