package resp.iam;

import resp.general.ResponseAbstractList;
import org.json.JSONArray;

public class ResponseIAMList extends ResponseAbstractList {

    public ResponseIAMList(JSONArray iamStreams) {
        super(iamStreams);
    }

    public JSONArray getIAMStreams() {
        return getList();
    }
}
