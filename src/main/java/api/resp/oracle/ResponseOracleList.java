package api.resp.oracle;

import api.resp.general.ResponseAbstractList;
import org.json.JSONArray;

public class ResponseOracleList extends ResponseAbstractList {

    public ResponseOracleList(JSONArray oracles) {
        super(oracles);
    }

    public JSONArray getOracles() {
        return getList();
    }
}
