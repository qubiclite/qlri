package resp;

import iam.IAMWriter;
import main.Configs;
import main.Main;
import resp.general.ResponseSuccess;
import tangle.TangleAPI;

public class ResponseNodeInfo extends ResponseSuccess {

    public ResponseNodeInfo() {
        obj.put("version", Main.VERSION);
        obj.put("iota_node", TangleAPI.getInstance().getNodeAddress());
        obj.put("testnet", Configs.getInstance().isTestnet());
    }

    public String getVersion() {
        return obj.getString("version");
    }

    public String getIOTANode() {
        return obj.getString("iota_node");
    }

    public boolean isOnTestnet() {
        return obj.getBoolean("testnet");
    }
}
