package commands.qubic;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import qubic.QubicWriter;

import java.util.ArrayList;

public class CommandQubicList extends Command {

    public static final CommandQubicList instance = new CommandQubicList();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(1, 81).setName("qubic id filter").setExampleValue("G9").setDescription("filters the list and only shows the qubics starting with this sequence").makeOptional()
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
    public void perform(Persistence persistence, String[] par) {

        String handle = par.length <= 1 ? "" : par[1];
        ArrayList<QubicWriter> qws = persistence.findAllQubicWritersWithHandle(handle);

        println("found " + qws.size() + " qubic(s):");
        for(QubicWriter qw : qws)
            println("   > " + qw.getID());
    }
}
