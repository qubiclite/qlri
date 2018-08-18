package commands.iam;

import resp.general.ResponseAbstract;
import resp.general.ResponseSuccess;
import resp.iam.ResponseIAMList;
import resp.iam.ResponseIAMRead;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.IntegerValidator;
import commands.param.validators.TryteValidator;
import iam.IAMIndex;
import main.Persistence;
import org.json.JSONArray;
import org.json.JSONObject;
import iam.IAMReader;
import tangle.TryteTool;

import java.util.Map;

public class CommandIAMRead extends ComandIAMAbstract {

    public static final CommandIAMRead instance = new CommandIAMRead();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(81, 81).setName("id").setExampleValue("CLUZILAWASDZAPQXWQHWRUBNXDFITUDFMBSBVAGB9PVLWDSYADZBPXCIOAYOEYAETUUNHNW9R9TZKU999").setDescription("IAM stream you want to read"),
            new IntegerValidator(0, Integer.MAX_VALUE).setName("index").setExampleValue("17").setDescription("index from which to read the message")
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
        return "Reads the message of an IAM stream at a certain index.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        JSONObject read = ((ResponseIAMRead)response).getRead();
        println(read == null ? "no message found in this iam stream at the requested index" : read.toString());
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        String iamId = (String)parMap.get("id");
        int index = (int)parMap.get("index");

        IAMReader iamReader = new IAMReader(iamId);
        JSONObject read = iamReader.read(new IAMIndex(index));

        return new ResponseIAMRead(read);
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {
        JSONObject o = new JSONObject();
        o.put("name", "penguin");
        o.put("habit", "antarctica");
        return new ResponseIAMRead(o);
    }
}
