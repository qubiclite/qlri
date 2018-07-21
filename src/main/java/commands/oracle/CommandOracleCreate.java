package commands.oracle;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.oracle.ResponseOracleCreate;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import exceptions.InvalidQubicTransactionException;
import main.Persistence;
import oracle.OracleManager;
import oracle.OracleWriter;
import qubic.QubicReader;

import java.util.Map;

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
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("started oracle with id: '" + ((ResponseOracleCreate)response).getOracleID() + "'");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        String qubicID = (String)parMap.get("qubic_id");
        QubicReader qr;
        try {
            qr = new QubicReader(qubicID);
        } catch (InvalidQubicTransactionException e) {
            return new ResponseError(e);
        }

        OracleWriter ow = new OracleWriter(qr);
        OracleManager om = new OracleManager(ow);
        persistence.addOracleWriter(ow);
        om.start();

        return new ResponseOracleCreate(ow.getID());
    }
}
