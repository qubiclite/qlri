package api.resp;

import api.resp.general.ResponseAbstract;
import org.json.JSONArray;

public class ResponseFetchEpoch extends ResponseAbstract {

    public ResponseFetchEpoch(JSONArray fetchedEpochs, int lastCompletedEpoch) {
        obj.put("fetched_epochs", fetchedEpochs);
        obj.put("last_completed_epoch", lastCompletedEpoch);
    }

    public JSONArray getFetchedEpochs() {
        return obj.getJSONArray("fetched_epochs");
    }

    public int getLastCompletedEpoch() {
        return obj.getInt("last_completed_epoch");
    }
}
