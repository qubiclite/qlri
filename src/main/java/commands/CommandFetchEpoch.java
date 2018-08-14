package commands;

import api.resp.ResponseFetchEpoch;
import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseSuccess;
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
import tangle.TryteTool;

import java.util.Map;

public class CommandFetchEpoch extends Command {

    public static final CommandFetchEpoch instance = new CommandFetchEpoch();

    private static ParameterValidator PV_QUBIC = new TryteValidator(81, 81).setName("qubic").setExampleValue(TryteTool.generateRandom(81)).setDescription("qubic to fetch from"),
            PV_EPOCH = new IntegerValidator(0, Integer.MAX_VALUE).setName("epoch").setExampleValue("4").setDescription("epoch to fetch"),
            PV_EPOCH_MAX = new IntegerValidator(0, Integer.MAX_VALUE).setName("epoch max").setExampleValue("7").setDescription("if used will fetch all epochs from '"+PV_EPOCH.getName()+"' up to this value").makeOptional(-1);

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{ PV_QUBIC, PV_EPOCH, PV_EPOCH_MAX });

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
        return "Determines the quorum based result (consensus) of a qubic's epoch.";
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
        String qubicId = (String)parMap.get(PV_QUBIC.getJSONKey());
        int epoch_min = (int)parMap.get(PV_EPOCH.getJSONKey());
        int epoch_max = (int)parMap.get(PV_EPOCH_MAX.getJSONKey());
        QubicReader qr = new QubicReader(qubicId);
        int lastCompletedEpoch = qr.lastCompletedEpoch();

        epoch_max = Math.max(epoch_min, epoch_max);
        epoch_max = Math.min(epoch_max, lastCompletedEpoch);

        JSONArray arr = new JSONArray();

        for(int epoch = epoch_min; epoch <= epoch_max; epoch++) {
            QuorumBasedResult qbr = InterQubicResultFetcher.fetchResult(qr, epoch);
            JSONObject fetchedEpoch = new JSONObject();
            fetchedEpoch.put("epoch", epoch);
            fetchedEpoch.put("quorum", qbr.getQuorum());
            fetchedEpoch.put("quorum_max", qbr.getQuorumMax());
            fetchedEpoch.put("result", qbr.getResult());
            arr.put(fetchedEpoch);
        }

        return new ResponseFetchEpoch(arr, lastCompletedEpoch);
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {
        JSONArray arr = new JSONArray();

        for(int epoch = 7; epoch <= 9; epoch++) {
            JSONObject fetchedEpoch = new JSONObject();
            fetchedEpoch.put("epoch", epoch);
            fetchedEpoch.put("quorum", epoch == 8 ? 1 : 2);
            fetchedEpoch.put("quorum_max", 3);
            fetchedEpoch.put("result", epoch == 8 ? null : "" + epoch * epoch);
            arr.put(fetchedEpoch);
        }

        return new ResponseFetchEpoch(arr, 815);
    }
}
