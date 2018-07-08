package commands.iam;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.IntegerValidator;
import commands.param.validators.TryteValidator;
import main.Main;
import main.Persistence;
import org.json.JSONException;
import org.json.JSONObject;
import tangle.IAMPublisher;

public class CommandIAMWrite extends Command {

    public static final CommandIAMWrite instance = new CommandIAMWrite();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("iam stream handle").setExampleValue("MB").setDescription("writes to the iam stream that starts with this tryte sequence"),
            new IntegerValidator(0, Integer.MAX_VALUE).setName("index").setExampleValue("17").setDescription("index at which you want to write"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "iam_write";
    }

    @Override
    public String getAlias() {
        return "iw";
    }

    @Override
    public String getDescription() {
        return "writes a message into the iam stream to a certain index";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {

        String handle = par[1];
        int index = Integer.parseInt(par[2]);
        IAMPublisher ip = persistence.findIAMStreamByHandle(handle);

        if(ip != null) {
            println("please enter the json object you want to publish (e.g: \"{'name': 'bob', 'age': 42, 'jobs': ['chief', 'dad']}\")\n");
            String input = Main.input();
            println("");

            JSONObject o;

            try {
                o = new JSONObject(input);
            } catch (JSONException e) {
                Main.println("you did not enter a json object, action aborted");
                return;
            }

            println("writing into IAM stream ...");
            ip.publish(index, o);
        }
    }
}
