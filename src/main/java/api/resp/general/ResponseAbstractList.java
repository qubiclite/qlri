package api.resp.general;

import org.json.JSONArray;

public abstract class ResponseAbstractList extends ResponseSuccess {

    public ResponseAbstractList(JSONArray arr) {
        obj.put("list", arr);
    }

    public JSONArray getList() {
        return obj.getJSONArray("list");
    }
}
