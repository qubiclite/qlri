package commands.iam;

import resp.general.ResponseAbstract;
import resp.general.ResponseSuccess;
import resp.iam.ResponseIAMList;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import iam.IAMWriter;
import main.Persistence;
import org.json.JSONArray;
import tangle.TryteTool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CommandIAMList extends ComandIAMAbstract {

    public static final CommandIAMList instance = new CommandIAMList();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{ });

    private static final CallValidator CV_TERMINAL = new CallValidator(new ParameterValidator[]{
            new TryteValidator(0, 81).setName("filter").setExampleValue("UW").setDescription("filters the list and only returns IAM streams starting with this sequence").makeOptional("")
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
        return "iam_list";
    }

    @Override
    public String getAlias() {
        return "il";
    }

    @Override
    public String getDescription() {
        return "List all IAM streams stored in the persistence.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        JSONArray iamStreams = ((ResponseIAMList)response).getIAMStreams();

        println("found " + iamStreams.length() + " IAM stream(s):");
        for(int i = 0; i < iamStreams.length(); i++)
            println("   > " + iamStreams.get(i));
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {
        JSONArray arr = new JSONArray();
        arr.put(TryteTool.generateRandom(81));
        arr.put(TryteTool.generateRandom(81));
        return new ResponseIAMList(arr);
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String filter = (String)parMap.get("filter");
        List<IAMWriter> ips = persistence.findAllIAMStreamsWithHandle(filter != null ? filter : "");
        JSONArray arr = new JSONArray();
        for(IAMWriter ip : ips)
            arr.put(ip.getID());
        return new ResponseIAMList(arr);
    }
}
