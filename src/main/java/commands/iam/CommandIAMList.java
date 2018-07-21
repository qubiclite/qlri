package commands.iam;

import api.resp.general.ResponseAbstract;
import api.resp.iam.ResponseIAMList;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import org.json.JSONArray;
import tangle.IAMPublisher;

import java.util.ArrayList;
import java.util.Map;

public class CommandIAMList extends Command {

    public static final CommandIAMList instance = new CommandIAMList();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(1, 81).setName("iam stream id filter").setExampleValue("UW").setDescription("filters the list and only shows the oracles starting with this sequence").makeOptional("")
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
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        JSONArray iamStreams = ((ResponseIAMList)response).getIAMStreams();

        println("found " + iamStreams.length() + " IAM stream(s):");
        for(int i = 0; i < iamStreams.length(); i++)
            println("   > " + iamStreams.get(i));
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String iamStreamIDFilter = (String)parMap.get("iam_stream_id_filter");
        ArrayList<IAMPublisher> ips = persistence.findAllIAMStreamsWithHandle(iamStreamIDFilter);
        JSONArray arr = new JSONArray();
        for(IAMPublisher ip : ips)
            arr.put(ip.getID());
        return new ResponseIAMList(arr);
    }
}
