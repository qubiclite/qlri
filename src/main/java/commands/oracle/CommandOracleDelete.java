package commands.oracle;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import oracle.OracleWriter;

public class CommandOracleDelete extends Command {

    public static final CommandOracleDelete instance = new CommandOracleDelete();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(1, 81).setName("oracle handle").setExampleValue("JR").setDescription("deletes the oracle that starts with this tryte sequence"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "oracle_delete";
    }

    @Override
    public String getAlias() {
        return "od";
    }

    @Override
    public String getDescription() {
        return "removes an oracle from the persistence (oracle's private key will be deleted: cannot be undone)";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {

        String handle = par[1];
        OracleWriter ow = persistence.findOracleWriterByHandle(handle);

        if(ow != null) {
            ow.getManager().terminate();
            persistence.deleteOracleWriter(ow);
            println("oracle writer '" + ow.getID() + "' deleted from persistence, will be terminated after epoch ends");
        }
    }
}
