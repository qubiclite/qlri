package commands.qubic;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.qubic.ResponseQubicListApplications;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import constants.TangleJSONConstants;
import main.Main;
import main.Persistence;
import org.json.JSONArray;
import org.json.JSONObject;
import qubic.QubicWriter;

import java.util.Map;

public class CommandQubicListApplications extends Command {

    public static final CommandQubicListApplications instance = new CommandQubicListApplications();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(1, 81).setName("qubic handle").setExampleValue("G9").setDescription("the qubic from which you want to read and list all applications"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "qubic_list_applications";
    }

    @Override
    public String getAlias() {
        return "qla";
    }

    @Override
    public String getDescription() {
        return "lists all incoming oracle applications for a specific qubic, basis for '" + CommandQubicAssemblyAdd.instance.getName() + "'";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        JSONArray applications = ((ResponseQubicListApplications)response).getList();
        println("found " + applications.length() + " application(s):");
        for (int i = 0; i < applications.length(); i++) {
            JSONObject application = applications.getJSONObject(i);
            String oracleID = escapeInput(application.getString(TangleJSONConstants.ORACLE_ID));
            String oracleName = escapeInput(application.getString(TangleJSONConstants.ORACLE_NAME));
            println("  > " + oracleID + " \"" + oracleName + "\"");
        }
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String qubic = (String)parMap.get("qubic_handle");
        QubicWriter qw = persistence.findQubicWriterByHandle(qubic);
        if(qw == null)
            return new ResponseError("you do not own a qubic with the id '"+qubic+"'");

        qw.fetchApplications();
        JSONArray arr = new JSONArray();
        for (JSONObject o : qw.getApplications())
            arr.put(o);
        return new ResponseQubicListApplications(arr);
    }

    private static String escapeInput(String raw) {
        return raw.replace("\\", "\\\\");
    }
}
