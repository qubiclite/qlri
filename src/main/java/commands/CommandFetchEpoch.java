package commands;

import api.resp.ResponseFetchEpoch;
import api.resp.general.ResponseAbstract;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.IntegerValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import oracle.QuorumBasedResult;
import org.json.JSONArray;
import org.json.JSONObject;
import qlvm.InterQubicResultFetcher;
import qubic.QubicReader;

import java.util.Map;

public class CommandFetchEpoch extends Command {

    public static final CommandFetchEpoch instance = new CommandFetchEpoch();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(81, 81).setName("qubic id").setExampleValue("KSU9E…SZ999").setDescription("IAM stream identity of the qubic to fetch from"),
            new IntegerValidator(0, Integer.MAX_VALUE).setName("epoch index").setExampleValue("4").setDescription("epoch to fetch"),
            new IntegerValidator(0, Integer.MAX_VALUE).setName("epoch index max").setExampleValue("7").setDescription("will fetch all epochs from 'epoch index' to 'epoch index max' if this parameter is set").makeOptional(-1)
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "fetch_epoch";
    }

    @Override
    public String getAlias() {
        return "fe";
    }

    @Override
    public String getDescription() {
        return "determines the quorum based result (which can be considered the consensus) of any qubic at any epoch";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        JSONArray fetchedEpochs = ((ResponseFetchEpoch)response).getFetchedEpochs();

        for(int i = 0; i < fetchedEpochs.length(); i++) {
            JSONObject fetchedEpoch = fetchedEpochs.getJSONObject(i);

            String result = fetchedEpoch.getString("result");
            int epoch = fetchedEpoch.getInt("epoch");
            double quorum = fetchedEpoch.getDouble("quorum");
            double quorumMax = fetchedEpoch.getDouble("quorum_max");

            println("--- EPOCH #" + epoch + " ---");
            println("RESULT: " + result);
            double percentage = Math.round(1000 * quorum / quorumMax) / 10;
            println("QUORUM: " + quorum + " / " + quorumMax + " ("+(percentage)+"%)");
            println("");
        }
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String qubicId = (String)parMap.get("qubic_id");
        int epoch_min = (int)parMap.get("epoch_index");
        int epoch_max = (int)parMap.get("epoch_index_max");
        QubicReader qr = new QubicReader(qubicId);
        int lastCompletedEpoch = qr.lastCompletedEpoch();

        if(epoch_max > lastCompletedEpoch) {
            epoch_max = lastCompletedEpoch;
        }

        JSONArray arr = new JSONArray();

        for(int epoch = epoch_min; epoch <= epoch_max; epoch++) {
            QuorumBasedResult qbr = InterQubicResultFetcher.fetchResult(qr, epoch);
            JSONObject fetchedEpoch = new JSONObject();
            fetchedEpoch.put("epoch", epoch);
            fetchedEpoch.put("quorum", qbr.getQuorum());
            fetchedEpoch.put("quorum_max", qbr.getQuorumMax());
            fetchedEpoch.put("result", qbr.getResult());
            arr.put(epoch-epoch_min, fetchedEpoch);
        }

        return new ResponseFetchEpoch(arr, lastCompletedEpoch);
    }
}
