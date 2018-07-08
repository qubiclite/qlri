package commands.qubic;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Main;
import main.Persistence;
import qubic.QubicWriter;

public class CommandQubicAssemblyAdd extends Command {

    public static final CommandQubicAssemblyAdd instance = new CommandQubicAssemblyAdd();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("qubic handle").setExampleValue("G9").setDescription("the qubic to whose assembly the oracle shall be added (finds the qubic starting with this tryte sequence)"),
            new TryteValidator(81, 81).setName("qubic id").setExampleValue("TEL9U…FH999").setDescription("IAM stream identity of the oracle to add to the assembly"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "qubic_assembly_add";
    }

    @Override
    public String getAlias() {
        return "qaa";
    }

    @Override
    public String getDescription() {
        return "adds an oracle to the assembly as preparation for '" + CommandQubicAssemble.instance.getName() + "'";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {

        String qubicHandle = par[1];
        String oracleId = par[2];
        QubicWriter qw = persistence.findQubicWriterByHandle(qubicHandle);

        if (qw != null) {
            qw.addToAssembly(oracleId);
            Main.println("added oracle '" + oracleId + "' to qubic assembly");
        }
    }
}
