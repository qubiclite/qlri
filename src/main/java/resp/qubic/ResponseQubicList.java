package resp.qubic;

import resp.general.ResponseAbstractList;
import org.json.JSONArray;

public class ResponseQubicList extends ResponseAbstractList {

    public ResponseQubicList(JSONArray qubics) {
        super(qubics);
    }

    public JSONArray getQubics() {
        return getList();
    }
}
