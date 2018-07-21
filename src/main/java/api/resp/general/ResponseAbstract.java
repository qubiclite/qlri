package api.resp.general;

import org.json.JSONObject;

public abstract class ResponseAbstract {

    protected final JSONObject obj = new JSONObject();

    public ResponseAbstract() {
        obj.put("success", true);
    }

    @Override
    public String toString() {
        return obj.toString();
    }

    public JSONObject toJSON() {
        return obj;
    }
}
