package api.resp.qubic;

import api.resp.general.ResponseAbstract;
import org.json.JSONArray;
import qubic.QubicReader;

public class ResponseQubicRead extends ResponseAbstract {

    public ResponseQubicRead(QubicReader qr) {
        obj.put("id", qr.getID());
        obj.put("application_address", qr.getApplicationAddress());
        obj.put("code", qr.getCode());
        obj.put("version", qr.getVersion());

        obj.put("execution_start", qr.getExecutionStart());
        obj.put("hash_period_duration", qr.getHashPeriodDuration());
        obj.put("result_period_duration", qr.getResultPeriodDuration());
        obj.put("runtime_limit", qr.getRunTimeLimit());

        obj.put("assembly_list", qr.getAssemblyList());
    }

    public String getID() {
        return obj.getString("id");
    }

    public String getCode() {
        return obj.getString("code");
    }

    public String getVersion() {
        return obj.getString("version");
    }

    public String getApplicationAddress() {
        return obj.getString("application_address");
    }

    public long getExecutionStart() {
        return obj.getLong("execution_start");
    }

    public long getHashPeriodDuration() {
        return obj.getLong("hash_period_duration");
    }

    public long getResultPeriodDuration() {
        return obj.getLong("result_period_duration");
    }

    public long getRuntimeLimit() {
        return obj.getLong("runtime_limit");
    }

    public JSONArray getAssemblyList() {
        return obj.getJSONArray("assembly_list");
    }
}
