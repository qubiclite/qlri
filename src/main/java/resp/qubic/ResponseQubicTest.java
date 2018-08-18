package resp.qubic;

import resp.general.ResponseAbstract;
import resp.general.ResponseSuccess;

public class ResponseQubicTest extends ResponseSuccess {

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
