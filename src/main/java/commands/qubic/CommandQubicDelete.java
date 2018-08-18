package commands.qubic;

import resp.general.ResponseAbstract;
import resp.general.ResponseError;
import resp.general.ResponseSuccess;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import qubic.QubicWriter;

import java.util.Map;

public class CommandQubicDelete extends CommandQubicAbstract {

    public static final CommandQubicDelete instance = new CommandQubicDelete();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(81,81).setName("qubic").setDescription("deletes the qubic that starts with this tryte sequence"),
    });

    private static final CallValidator CV_TERMINAL = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("qubic").setDescription("deletes the qubic that starts with this tryte sequence"),
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
        return "qubic_delete";
    }

    @Override
    public String getAlias() {
        return "qd";
    }

    @Override
    public String getDescription() {
        return "Removes a qubic from the persistence (private key will be deleted: cannot be undone).";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("qubic deleted from persistence");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String qubicID = (String)parMap.get("qubic");
        QubicWriter qw = persistence.findQubicWriterByHandle(qubicID);
        if(qw == null)
            return new ResponseError("you do not own a qubic with the id '"+qubicID+"'");

        persistence.deleteQubicWriter(qw);
        return new ResponseSuccess();
    }
}
