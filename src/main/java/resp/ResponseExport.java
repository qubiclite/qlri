package resp;

import iam.IAMWriter;
import resp.general.ResponseSuccess;

public class ResponseExport extends ResponseSuccess {

    public ResponseExport(String prefix, IAMWriter writer) {
        String exportString = prefix + "_" + writer.getID() + "_" + writer.getPrivateKeyTrytes();
        obj.put("export", exportString);
    }

    public String getExport() {
        return obj.getString("export");
    }
}
