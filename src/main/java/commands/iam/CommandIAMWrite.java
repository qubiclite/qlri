package commands.iam;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.general.ResponseSuccess;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.*;
import main.Main;
import main.Persistence;
import org.json.JSONException;
import org.json.JSONObject;
import tangle.IAMPublisher;

import java.util.ArrayList;
import java.util.Map;

public class CommandIAMWrite extends Command {

    public static final CommandIAMWrite instance = new CommandIAMWrite();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("iam stream handle").setExampleValue("MB").setDescription("writes to the iam stream that starts with this tryte sequence"),
            new IntegerValidator(0, Integer.MAX_VALUE).setName("index").setExampleValue("17").setDescription("index at which you want to write"),
            new JSONObjectValidator().setName("message").setExampleValue("{'day': 4}").setDescription("the json object to write into the stream")
    });

    private static final CallValidator CV_TERMINAL = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("iam stream handle").setExampleValue("MB").setDescription("writes to the iam stream that starts with this tryte sequence"),
            new IntegerValidator(0, Integer.MAX_VALUE).setName("index").setExampleValue("17").setDescription("index at which you want to write"),
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
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        String handle = (String)parMap.get("iam_stream_handle");
        int index = (int)parMap.get("index");
        JSONObject message = (JSONObject)parMap.get("message");

        ArrayList<IAMPublisher> ips = persistence.findAllIAMStreamsWithHandle(handle);
        if(ips.size() != 1)
            return new ResponseError("there are "+ips.size()+" iam streams with the handle '"+handle+"'");

        if(message == null) {
            println("please enter the json object you want to publish (e.g: \"{'name': 'bob', 'age': 42, 'jobs': ['chief', 'dad']}\")\n");
            String input = Main.input();
            println("");

            try {
                message = new JSONObject(input);
            } catch (JSONException e) {
                return new ResponseError("you did not enter a json object, action aborted");
            }
        }

        ips.get(0).publish(index, message);
        return new ResponseSuccess();
    }
}
