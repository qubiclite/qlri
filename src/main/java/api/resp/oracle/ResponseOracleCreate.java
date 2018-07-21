package api.resp.oracle;

import api.resp.general.ResponseAbstract;

public class ResponseOracleCreate extends ResponseAbstract {

    public ResponseOracleCreate(String oracleID) {
        obj.put("oracle_id", oracleID);
    }

    public String getOracleID() {
        return obj.getString("oracle_id");
    }
}
