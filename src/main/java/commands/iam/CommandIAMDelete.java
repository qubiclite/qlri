package commands.iam;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import tangle.IAMPublisher;

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
    public void perform(Persistence persistence, String[] par) {

        String handle = par[1];
        IAMPublisher ip = persistence.findIAMStreamByHandle(handle);

        if(ip != null) {
            persistence.deleteIAMPublisher(ip);
            println("deleted iam stream from persistence: " + ip.getID());
        }
    }
}
