package api.resp.qubic;

import api.resp.general.ResponseAbstract;

public class ResponseQubicTest extends ResponseAbstract {

    public ResponseQubicTest(String result, long runtime) {
        obj.put("result", result);
        obj.put("runtime", runtime);
    }

    public long getRuntime() {
        return obj.getLong("runtime");
    }

    public String getResult() {
        return obj.getString("result");
    }
}
