package api.resp.iam;

import api.resp.general.ResponseAbstract;
import org.json.JSONObject;

public class ResponseIAMRead extends ResponseAbstract {

    public ResponseIAMRead(JSONObject read) {
        obj.put("read", read);
    }

    public JSONObject getRead() {
        return obj.getJSONObject("read");
    }
}
