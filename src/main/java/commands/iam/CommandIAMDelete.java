package commands.iam;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.general.ResponseSuccess;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import tangle.IAMPublisher;

import java.util.Map;

public class CommandIAMDelete extends Command {

    public static final CommandIAMDelete instance = new CommandIAMDelete();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("iam stream handle").setExampleValue("MB").setDescription("deletes the iam stream that starts with this tryte sequence"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "iam_delete";
    }

    @Override
    public String getAlias() {
        return "id";
    }

    @Override
    public String getDescription() {
        return "removes an IAM stream from the persistence (stream's private key will be deleted: cannot be undone)";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("IAM stream deleted from persistence");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String iamStreamHandle = (String)parMap.get("iam_stream_handle");
        IAMPublisher ip = persistence.findIAMStreamByHandle(iamStreamHandle);

        if(ip == null)
            return new ResponseError("you do not own an IAM stream with the id '"+iamStreamHandle+"'");

        persistence.deleteIAMPublisher(ip);
        return new ResponseSuccess();
    }
}
