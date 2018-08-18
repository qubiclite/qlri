package resp.iam;

import resp.general.ResponseAbstract;
import resp.general.ResponseSuccess;
import org.json.JSONObject;

public class ResponseIAMRead extends ResponseSuccess {

    public ResponseIAMRead(JSONObject read) {
        obj.put("read", read);
    }

    public JSONObject getRead() {
        return obj.getJSONObject("read");
    }
}
