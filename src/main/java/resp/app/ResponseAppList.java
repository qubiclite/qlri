package resp.app;

import resp.general.ResponseAbstractList;
import org.json.JSONArray;

public class ResponseAppList extends ResponseAbstractList {

    public ResponseAppList(JSONArray apps) {
        super(apps);
    }

    public JSONArray getApps() {
        return getList();
    }
}
