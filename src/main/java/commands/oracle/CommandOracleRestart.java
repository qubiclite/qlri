package commands.oracle;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import oracle.OracleManager;
import oracle.OracleWriter;

public class CommandOracleRestart extends Command {

    public static final CommandOracleRestart instance = new CommandOracleRestart();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("oracle handle").setExampleValue("JR").setDescription("restarts the oracle that starts with this tryte sequence"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "oracle_restart";
    }

    @Override
    public String getAlias() {
        return "or";
    }

    @Override
    public String getDescription() {
        return "restarts an oracle that was paused with '"+ CommandOraclePause.instance.getName() +"', makes it process its qubic again";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {

        String handle = par[1];
        OracleWriter ow = persistence.findOracleWriterByHandle(handle);

        if(ow != null) {
            println("restarting oracle");
            new OracleManager(ow).start();
        }
    }
}
