package commands.iam;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.general.ResponseSuccess;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import iam.IAMWriter;
import main.Persistence;

import java.util.Map;

public class CommandIAMDelete extends ComandIAMAbstract {

    public static final CommandIAMDelete instance = new CommandIAMDelete();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(81, 81).setName("id").setExampleValue("XUYRQFPGFAMCNNRE9BMGYDWNTXLKWQBYYECSMZAMQFGHTUHSIYKVPDOUOCTUKQPMRGYF9IJSXIMKMAEL9").setDescription("IAM stream ID"),
    });

    private static final CallValidator CV_TERMINAL = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("id").setExampleValue("MB").setDescription("deletes the iam stream that starts with this tryte sequence"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public CallValidator getCallValidatorForTerminal() {
        return CV_TERMINAL;
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
        return "Removes an IAM stream from the persistence (private key will be deleted, cannot be undone).";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("IAM stream deleted from persistence");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String iamStreamHandle = (String)parMap.get("id");
        IAMWriter ip = persistence.findIAMStreamByHandle(iamStreamHandle);

        if(ip == null)
            return new ResponseError("you do not own an IAM stream with the id '"+iamStreamHandle+"'");

        persistence.deleteIAMPublisher(ip);
        return new ResponseSuccess();
    }
}
