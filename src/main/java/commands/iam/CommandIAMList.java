package commands.iam;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import tangle.IAMPublisher;

import java.util.ArrayList;

public class CommandIAMList extends Command {

    public static final CommandIAMList instance = new CommandIAMList();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("iam stream id filter").setExampleValue("UW").setDescription("filters the list and only shows the oracles starting with this sequence").makeOptional()
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "iam_list";
    }

    @Override
    public String getAlias() {
        return "il";
    }

    @Override
    public String getDescription() {
        return "prints the full list of all IAM streams stored in the persistence";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {


        String handle = par.length <= 1 ? "" : par[1];
        ArrayList<IAMPublisher> ips = persistence.findAllIAMStreamsWithHandle(handle);

        println("found " + ips.size() + " IAM stream(s):");
        for(IAMPublisher ip : ips)
            println("   > " + ip.getID());
    }
}
