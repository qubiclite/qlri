package commands.oracle;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import oracle.OracleWriter;

import java.util.ArrayList;

public class CommandOracleList extends Command {

    public static final CommandOracleList instance = new CommandOracleList();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(1, 81).setName("oracle id filter").setExampleValue("JR").setDescription("filters the list and only shows the oracles starting with this sequence").makeOptional()
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "oracle_list";
    }

    @Override
    public String getAlias() {
        return "ol";
    }

    @Override
    public String getDescription() {
        return "prints the full list of all oracles stored in the persistence";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {

        String handle = par.length <= 1 ? "" : par[1];
        ArrayList<OracleWriter> ows = persistence.findAllOracleWritersWithHandle(handle);

        println("found " + ows.size() + " oracle(s):");
        for(OracleWriter ow : ows)
            println("   > " + ow.getID());
    }
}
