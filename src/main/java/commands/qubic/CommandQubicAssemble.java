package commands.qubic;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import qubic.QubicWriter;

public class CommandQubicAssemble extends Command {

    public static final CommandQubicAssemble instance = new CommandQubicAssemble();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("qubic handle").setExampleValue("G9").setDescription("the qubic that shall publish its assembly transaction"),
    });

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
        return "publishes the assembly transaction for a specific qubic";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {

        println("creating assembly transaction ...");

        String handle = par[1];
        QubicWriter qw = persistence.findQubicWriterByHandle(handle);
        qw.publishAssemblyTx();

        println("assembly transaction created: '"+qw.getAssemblyTxHash()+"'");
    }
}
