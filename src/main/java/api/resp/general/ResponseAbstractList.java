package api.resp.general;

import org.json.JSONArray;

public abstract class ResponseAbstractList extends ResponseAbstract {

    public ResponseAbstractList(JSONArray arr) {
        obj.put("list", arr);
    }

    public JSONArray getList() {
        return obj.getJSONArray("list");
    }
}
