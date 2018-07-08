package commands.oracle;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import oracle.OracleManager;
import oracle.OracleWriter;
import qubic.QubicReader;

public class CommandOracleCreate extends Command {

    public static final CommandOracleCreate instance = new CommandOracleCreate();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(81, 81).setName("qubic id").setExampleValue("KSU9E…SZ999").setDescription("IAM stream identity of the qubic you want your oracle to process"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "oracle_create";
    }

    @Override
    public String getAlias() {
        return "oc";
    }

    @Override
    public String getDescription() {
        return "creates a new oracle and stores it in the persistence. life cycle will be automized, no need to do anything from here on";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {
        println("creating oracle ...");

        String qubicId = par[1];
        OracleWriter ow = new OracleWriter(new QubicReader(qubicId));
        OracleManager om = new OracleManager(ow);
        persistence.addOracleWriter(ow);
        om.start();

        println("started oracle with id: '" + ow.getID() + "'");
    }
}
