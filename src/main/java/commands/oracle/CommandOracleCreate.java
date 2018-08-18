package commands.oracle;

import resp.general.ResponseAbstract;
import resp.general.ResponseError;
import resp.general.ResponseSuccess;
import resp.oracle.ResponseOracleCreate;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import exceptions.InvalidQubicTransactionException;
import main.IAMWriterStock;
import main.Persistence;
import oracle.OracleManager;
import oracle.OracleWriter;
import qubic.QubicReader;
import tangle.TryteTool;

import java.util.Map;

public class CommandOracleCreate extends CommandOracleAbstract {

    public static final CommandOracleCreate instance = new CommandOracleCreate();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(81, 81).setName("qubic").setDescription("ID of the qubic which shall be processed by this oracle."),
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
        return "Creates a new oracle and stores it in the persistence. Life cycle will run automically, no more actions required from here on.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("started oracle with id: '" + ((ResponseOracleCreate)response).getOracleID() + "'");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        String qubicID = (String)parMap.get("qubic");
        QubicReader qr;
        try {
            qr = new QubicReader(qubicID);
        } catch (InvalidQubicTransactionException e) {
            return new ResponseError(e);
        }

        OracleWriter ow = new OracleWriter(qr, IAMWriterStock.receive());
        OracleManager om = new OracleManager(ow);
        persistence.addOracleWriter(ow);
        om.start();

        return new ResponseOracleCreate(ow.getID());
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {
        return new ResponseOracleCreate(TryteTool.generateRandom(81));
    }
}
