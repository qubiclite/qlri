package commands.iam;

import api.resp.general.ResponseAbstract;
import api.resp.iam.ResponseIAMCreate;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import main.Persistence;
import tangle.IAMPublisher;

import java.util.Map;

public class CommandIAMCreate extends Command {

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
        return "creates a new IAM stream and stores it in the persistence";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("iam stream created successfully: " + ((ResponseIAMCreate)response).getIAM_ID());
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        IAMPublisher iamPublisher = new IAMPublisher();
        persistence.addIAMPublisher(iamPublisher);
        return new ResponseIAMCreate(iamPublisher.getID());
    }
}
