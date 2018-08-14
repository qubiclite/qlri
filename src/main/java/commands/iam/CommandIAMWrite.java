package commands.iam;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.general.ResponseSuccess;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.*;
import iam.IAMIndex;
import main.Main;
import main.Persistence;
import org.json.JSONException;
import org.json.JSONObject;
import iam.IAMWriter;

import java.util.List;
import java.util.Map;

public class CommandIAMWrite extends ComandIAMAbstract {

    public static final CommandIAMWrite instance = new CommandIAMWrite();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(81, 81).setName("ID").setExampleValue("CLUZILAWASDZAPQXWQHWRUBNXDFITUDFMBSBVAGB9PVLWDSYADZBPXCIOAYOEYAETUUNHNW9R9TZKU999").setDescription("the IAM stream in which to write"),
            new IntegerValidator(0, Integer.MAX_VALUE).setName("index").setExampleValue("17").setDescription("index at which to write"),
            new JSONObjectValidator().setName("message").setExampleValue("{'day': 4}").setDescription("the json object to write into the stream")
    });

    private static final CallValidator CV_TERMINAL = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("ID").setExampleValue("MB").setDescription("writes to the iam stream that starts with this tryte sequence"),
            new IntegerValidator(0, Integer.MAX_VALUE).setName("index").setExampleValue("17").setDescription("index at which to write"),
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
        return "Writes a message into the iam stream at an index position.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        String id = (String)parMap.get("id");
        int index = (int)parMap.get("index");
        JSONObject message = (JSONObject)parMap.get("message");

        List<IAMWriter> ips = persistence.findAllIAMStreamsWithHandle(id);
        if(ips.size() != 1)
            return new ResponseError("there are "+ips.size()+" iam streams with the handle '"+id+"'");

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

        ips.get(0).publish(new IAMIndex(index), message);
        return new ResponseSuccess();
    }
}
