package commands.iam;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import main.Persistence;
import tangle.IAMPublisher;

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
    public void perform(Persistence persistence, String[] par) {
        println("creating new iam stream ...");
        IAMPublisher iamPublisher = new IAMPublisher();
        persistence.addIAMPublisher(iamPublisher);
        println("iam stream created successfully: " + iamPublisher.getID());
    }
}
