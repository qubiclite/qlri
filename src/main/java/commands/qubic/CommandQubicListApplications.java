package commands.qubic;

import resp.general.ResponseAbstract;
import resp.general.ResponseError;
import resp.general.ResponseSuccess;
import resp.qubic.ResponseQubicListApplications;
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
import tangle.TryteTool;

import java.util.Map;

public class CommandQubicListApplications extends CommandQubicAbstract {

    public static final CommandQubicListApplications instance = new CommandQubicListApplications();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(81, 81).setName("qubic").setDescription("the qubic of which you want to list all applications"),
    });

    private static final CallValidator CV_TERMINAL = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("qubic").setDescription("the qubic of which you want to list all applications"),
    });

    @Override
    public CallValidator getCallValidatorForTerminal() {
        return CV_TERMINAL;
    }

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
        return "Lists all incoming oracle applications for a specific qubic, response can be used for '" + CommandQubicAssemblyAdd.instance.getName() + "'.";
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
        String qubic = (String)parMap.get("qubic");
        QubicWriter qw = persistence.findQubicWriterByHandle(qubic);
        if(qw == null)
            return new ResponseError("you do not own a qubic with the id '"+qubic+"'");

        qw.fetchApplications();
        JSONArray arr = new JSONArray();
        for (JSONObject o : qw.fetchApplications())
            arr.put(o);
        return new ResponseQubicListApplications(arr);
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {
        JSONArray arr = new JSONArray();
        arr.put(TryteTool.generateRandom(81));
        arr.put(TryteTool.generateRandom(81));
        return new ResponseQubicListApplications(arr);
    }

    private static String escapeInput(String raw) {
        return raw.replace("\\", "\\\\");
    }
}
