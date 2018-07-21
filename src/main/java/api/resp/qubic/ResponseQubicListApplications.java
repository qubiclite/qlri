package api.resp.qubic;

import api.resp.general.ResponseAbstractList;
import org.json.JSONArray;

public class ResponseQubicListApplications extends ResponseAbstractList {

    public ResponseQubicListApplications(JSONArray applications) {
        super(applications);
    }

    public JSONArray getApplications() {
        return getList();
    }
}
