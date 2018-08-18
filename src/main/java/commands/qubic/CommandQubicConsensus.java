package commands.qubic;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.IntegerValidator;
import commands.param.validators.TryteValidator;
import iam.IAMIndex;
import main.Persistence;
import oracle.QuorumBasedResult;
import org.json.JSONArray;
import org.json.JSONObject;
import qlvm.InterQubicResultFetcher;
import qubic.QubicReader;
import resp.ResponseFetchEpoch;
import resp.general.ResponseAbstract;
import resp.general.ResponseSuccess;
import resp.qubic.ResponseQubicConsensus;
import tangle.TryteTool;

import java.util.Map;

public class CommandQubicConsensus extends Command {

    public static final CommandQubicConsensus instance = new CommandQubicConsensus();

    private static ParameterValidator PV_QUBIC = new TryteValidator(81, 81).setName("qubic").setExampleValue(TryteTool.generateRandom(81)).setDescription("qubic to find consensus in"),
            PV_KEYWORD = new TryteValidator(0, IAMIndex.MAX_KEYWORD_LENGTH).setName("keyword").setDescription("keyword of the iam index to find consensus for"),
            PV_POSITION = new IntegerValidator(0, Integer.MAX_VALUE).setName("position").setExampleValue("4").setDescription("position of the iam index to find consensus for");

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{ PV_QUBIC, PV_KEYWORD, PV_POSITION });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "qubic_consensus";
    }

    @Override
    public String getAlias() {
        return "qco";
    }

    @Override
    public String getDescription() {
        return "Determines the quorum based consensus of a qubic's oracle assembly at any IAM index.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        ResponseQubicConsensus responseQubicConsensus = (ResponseQubicConsensus)response;
        QuorumBasedResult qbr = responseQubicConsensus.getQuorumBasedResult();
        IAMIndex iamIndex = responseQubicConsensus.getIAMIndex();

        double quorum = qbr.getQuorum();
        double quorumMax = qbr.getQuorumMax();
        double percentage = Math.round(1000 * quorum / quorumMax) / 10;

        println("INDEX:  " + ( iamIndex.getKeyword().length() > 0 ? iamIndex.getKeyword() + " : " : "") + iamIndex.getPosition());
        println("RESULT: " + qbr.getResult());
        println("QUORUM: " + quorum + " / " + quorumMax + " ("+(percentage)+"%)");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String qubicId = (String)parMap.get(PV_QUBIC.getJSONKey());
        String keyword = (String)parMap.get(PV_KEYWORD.getJSONKey());
        int position = (int)parMap.get(PV_POSITION.getJSONKey());
        IAMIndex iamIndex = new IAMIndex(keyword, position);

        QuorumBasedResult qbr = InterQubicResultFetcher.fetchQubicConsensus(qubicId, iamIndex);
        return new ResponseQubicConsensus(iamIndex, qbr);
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {
        return new ResponseQubicConsensus(new IAMIndex("COLORS", 2018), new QuorumBasedResult(3, 4, "{'color': 'red'}"));
    }
}
