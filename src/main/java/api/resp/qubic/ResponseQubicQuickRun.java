package api.resp.qubic;

import api.resp.general.ResponseSuccess;

public class ResponseQubicQuickRun extends ResponseSuccess {

    public ResponseQubicQuickRun(String qubicID, String oracleID) {
        obj.put("qubic_id", qubicID);
        obj.put("oracle_id", oracleID);
    }

    public String getQubicID() {
        return obj.getString("qubic_id");
    }

    public String getOracleID() {
        return obj.getString("oracle_id");
    }
}
