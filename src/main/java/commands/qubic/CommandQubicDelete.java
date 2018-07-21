package commands.qubic;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.general.ResponseSuccess;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import qubic.QubicWriter;

import java.util.Map;

public class CommandQubicDelete extends Command {

    public static final CommandQubicDelete instance = new CommandQubicDelete();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(1, 81).setName("qubic handle").setExampleValue("G9").setDescription("deletes the qubic that starts with this tryte sequence"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "qubic_delete";
    }

    @Override
    public String getAlias() {
        return "qd";
    }

    @Override
    public String getDescription() {
        return "removes a qubic from the persistence (qubic's private key will be deleted: cannot be undone)";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("qubic deleted from persistence");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String qubicID = (String)parMap.get("qubic_handle");
        QubicWriter qw = persistence.findQubicWriterByHandle(qubicID);
        if(qw == null)
            return new ResponseError("you do not own a qubic with the id '"+qubicID+"'");

        persistence.deleteQubicWriter(qw);
        return new ResponseSuccess();
    }
}
