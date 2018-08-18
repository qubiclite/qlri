package resp.iam;

import resp.general.ResponseSuccess;

public class ResponseIAMCreate extends ResponseSuccess {

    public ResponseIAMCreate(String iamID) {
        obj.put("iam_id", iamID);
    }

    public String getIAM_ID() {
        return obj.getString("iam_id");
    }
}
