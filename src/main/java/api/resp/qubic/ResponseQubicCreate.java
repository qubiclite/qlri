package api.resp.qubic;

import api.resp.general.ResponseSuccess;

public class ResponseQubicCreate extends ResponseSuccess {

    public ResponseQubicCreate(String qubicID) {
        obj.put("qubic_id", qubicID);
    }

    public String getQubicID() {
        return obj.getString("qubic_id");
    }
}
