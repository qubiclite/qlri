package api.resp.iam;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseSuccess;
import org.json.JSONObject;

public class ResponseIAMRead extends ResponseSuccess {

    public ResponseIAMRead(JSONObject read) {
        obj.put("read", read);
    }

    public JSONObject getRead() {
        return obj.getJSONObject("read");
    }
}
