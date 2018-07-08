package commands.oracle;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import oracle.OracleWriter;

public class CommandOraclePause extends Command {

    public static final CommandOraclePause instance = new CommandOraclePause();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(1, 81).setName("oracle handle").setExampleValue("JR").setDescription("pauses the oracle that starts with this tryte sequence"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "oracle_pause";
    }

    @Override
    public String getAlias() {
        return "op";
    }

    @Override
    public String getDescription() {
        return "temporarily stops an oracle from processing its qubic, can be undone with '" + CommandOracleRestart.instance.getName() + "'";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {

        String handle = par[1];
        OracleWriter ow = persistence.findOracleWriterByHandle(handle);

        if(ow != null) {
            println("pausing oracle after epoch ends");
            ow.getManager().terminate();
        }
    }
}
