package commands.qubic;

import resp.general.ResponseAbstract;
import resp.general.ResponseSuccess;
import resp.qubic.ResponseQubicList;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import org.json.JSONArray;
import org.json.JSONObject;
import qubic.QubicWriter;

import java.util.List;
import java.util.Map;

public class CommandQubicList extends CommandQubicAbstract {

    public static final CommandQubicList instance = new CommandQubicList();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{ });

    private static final CallValidator CV_TERMINAL = new CallValidator(new ParameterValidator[]{
            new TryteValidator(0, 81).setName("filter").setDescription("filters the list and only shows the qubics starting with this sequence").makeOptional("")
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
        return "qubic_list";
    }

    @Override
    public String getAlias() {
        return "ql";
    }

    @Override
    public String getDescription() {
        return "Lists all qubics stored in the persistence.";
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
        String filter = (String)parMap.get("filter");
        List<QubicWriter> qws = persistence.findAllQubicWritersWithHandle(filter != null ? filter : "");
        JSONArray arr = new JSONArray();
        for(QubicWriter qw : qws)
            arr.put(generateListEntry(qw));
        return new ResponseQubicList(arr);
    }

    private JSONObject generateListEntry(QubicWriter qubicWriter) {
        JSONObject listEntry = new JSONObject();
        listEntry.put("id", qubicWriter.getID());
        listEntry.put("state", qubicWriter.getState());
        listEntry.put("specification", qubicWriter.getSpecification().generateQubicTransactionJSON());
        return listEntry;
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {
        JSONArray arr = new JSONArray();

        QubicWriter qw = new QubicWriter();
        qw.getEditable().setCode("return('hello world');");
        qw.publishQubicTransaction();

        arr.put(generateListEntry(qw));
        return new ResponseQubicList(arr);
    }
}
