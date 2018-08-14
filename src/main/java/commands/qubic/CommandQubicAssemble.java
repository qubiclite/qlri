package commands.qubic;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.general.ResponseSuccess;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.JSONArrayValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import org.json.JSONArray;
import qubic.QubicWriter;
import tangle.TryteTool;

import java.util.Map;

public class CommandQubicAssemble extends CommandQubicAbstract {

    public static final CommandQubicAssemble instance = new CommandQubicAssemble();

    private static final ParameterValidator  PV_QH = new TryteValidator(1, 81).setName("qubic handle").setDescription("the qubic that shall publish its assembly transaction");

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        PV_QH, new JSONArrayValidator().setName("assembly").setExampleValue("['"+TryteTool.generateRandom(81) +"', '"+TryteTool.generateRandom(81)+"']").setDescription("json array of the oracle IDs to be part of the assembly")
    });

    private static final CallValidator CV_TERMINAL = new CallValidator(new ParameterValidator[]{ PV_QH });

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
        return "qubic_assemble";
    }

    @Override
    public String getAlias() {
        return "qa";
    }

    @Override
    public String getDescription() {
        return "Publishes the assembly transaction for a specific qubic.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        String handle = par[1];
        QubicWriter qw = persistence.findQubicWriterByHandle(handle);

        println("assembly transaction created: '"+qw.getAssemblyTransactionHash()+"'");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        String qubicHandle = (String)parMap.get(PV_QH.getJSONKey());
        JSONArray assembly = (JSONArray)parMap.get("assembly");

        QubicWriter qw = persistence.findQubicWriterByHandle(qubicHandle);
        if(qw == null)
            return new ResponseError("you do not own a qubic with the id '"+qubicHandle+"'");

        if(assembly != null)
            for(int i = 0; i < assembly.length(); i++)
                qw.getAssembly().add(assembly.getString(i)); // TODO check if indeed is string

        try {
            qw.publishAssemblyTransaction();
        } catch (Exception e) {
            return new ResponseError(e.getClass().getName() + ": " + e.getMessage());
        }

        // TODO individualized response containing assembly tx hash
        return new ResponseSuccess();
    }
}
