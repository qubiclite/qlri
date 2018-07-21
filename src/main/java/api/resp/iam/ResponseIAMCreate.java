package api.resp.iam;

import api.resp.general.ResponseAbstract;

public class ResponseIAMCreate extends ResponseAbstract {

    public ResponseIAMCreate(String iamID) {
        obj.put("iam_id", iamID);
    }

    public String getIAM_ID() {
        return obj.getString("iam_id");
    }
}
