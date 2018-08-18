package resp.qubic;

import iam.IAMIndex;
import oracle.QuorumBasedResult;
import resp.general.ResponseSuccess;

public class ResponseQubicConsensus extends ResponseSuccess {

    public ResponseQubicConsensus(IAMIndex index, QuorumBasedResult qbr) {
        obj.put("result", qbr.getResult());
        obj.put("quorum", qbr.getQuorum());
        obj.put("quorum_max", qbr.getQuorumMax());
        obj.put("index_keyword", index.getKeyword());
        obj.put("index_position", index.getPosition());
    }

    public QuorumBasedResult getQuorumBasedResult() {
        return new QuorumBasedResult(getQuorum(), getQuorumMax(), getResult());
    }

    private String getResult() {
        return obj.getString("result");
    }
    private double getQuorum() {
        return obj.getDouble("quorum");
    }
    private double getQuorumMax() {
        return obj.getDouble("quorum_max");
    }

    public IAMIndex getIAMIndex() {
        return new IAMIndex(getIndexKeyword(), getIndexPosition());
    }

    private String getIndexKeyword() {
        return obj.getString("index_keyword");
    }
    private long getIndexPosition() {
        return obj.getLong("index_position");
    }
}
