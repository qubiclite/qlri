package commands.qubic;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import qubic.QubicWriter;

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
    public void perform(Persistence persistence, String[] par) {

        String handle = par[1];
        QubicWriter qw = persistence.findQubicWriterByHandle(handle);

        if(qw != null) {
            persistence.deleteQubicWriter(qw);
            println("qubic '" + qw.getID() + "' deleted from persistence");
        }
    }
}
