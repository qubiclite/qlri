package resp.oracle;

import resp.general.ResponseSuccess;

public class ResponseOracleCreate extends ResponseSuccess {

    public ResponseOracleCreate(String oracleID) {
        obj.put("oracle_id", oracleID);
    }

    public String getOracleID() {
        return obj.getString("oracle_id");
    }
}
