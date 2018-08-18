package commands.iam;

import resp.general.ResponseAbstract;
import resp.general.ResponseSuccess;
import resp.iam.ResponseIAMCreate;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import iam.IAMWriter;
import main.IAMWriterStock;
import main.Persistence;
import tangle.TryteTool;

import java.util.Map;

public class CommandIAMCreate extends ComandIAMAbstract {

    public static final CommandIAMCreate instance = new CommandIAMCreate();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{

    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "iam_create";
    }

    @Override
    public String getAlias() {
        return "ic";
    }

    @Override
    public String getDescription() {
        return "Creates a new IAM stream and stores it locally in the persistence.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("iam stream created successfully: " + ((ResponseIAMCreate)response).getIAM_ID());
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        IAMWriter iamWriter = IAMWriterStock.receive();
        persistence.addIAMWriter(iamWriter);
        return new ResponseIAMCreate(iamWriter.getID());
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {
        return new ResponseIAMCreate(TryteTool.generateRandom(81));
    }
}
