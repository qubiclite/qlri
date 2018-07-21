package commands.iam;

import api.resp.general.ResponseAbstract;
import api.resp.iam.ResponseIAMRead;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.IntegerValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import org.json.JSONObject;
import tangle.IAMReader;

import java.util.Map;

public class CommandIAMRead extends Command {

    public static final CommandIAMRead instance = new CommandIAMRead();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(81, 81).setName("iam id").setExampleValue("MBQUR…ZTG99").setDescription("identity of the IAM stream you want to read"),
            new IntegerValidator(0, Integer.MAX_VALUE).setName("index").setExampleValue("17").setDescription("index at which you want to read")
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "iam_read";
    }

    @Override
    public String getAlias() {
        return "ir";
    }

    @Override
    public String getDescription() {
        return "reads the message of an IAM stream at a certain index";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        JSONObject read = ((ResponseIAMRead)response).getRead();
        println(read == null ? "no message found in this iam stream at the requested index" : read.toString());
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        String iamId = (String)parMap.get("iam_id");
        int index = (int)parMap.get("index");

        IAMReader iamReader = new IAMReader(iamId);
        JSONObject read = iamReader.read(index);

        return new ResponseIAMRead(read);
    }
}
