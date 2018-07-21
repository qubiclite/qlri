package api.resp.qubic;

import api.resp.general.ResponseAbstract;

public class ResponseQubicCreate extends ResponseAbstract {

    public ResponseQubicCreate(String qubicID) {
        obj.put("qubic_id", qubicID);
    }

    public String getQubicID() {
        return obj.getString("qubic_id");
    }
}
