package commands;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.general.ResponseSuccess;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.IntegerValidator;
import commands.param.validators.NodeAddressValidator;
import main.Configs;
import main.Persistence;
import tangle.TangleAPI;

import java.util.Map;

public class CommandChangeNode extends Command {

    public static final CommandChangeNode instance = new CommandChangeNode();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new NodeAddressValidator().setName("node address").setExampleValue("https://node.example.org:14265").setDescription("address to any node api (mainnet or testnet, depending on what ql-nodes you want to connect to)"),
        new IntegerValidator(9, 14).setName("min weight magnitude").setExampleValue("14").setDescription("always use 14, use 9 only when connecting to a testnet node").makeOptional(14),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "change_node";
    }

    @Override
    public String getAlias() {
        return "cn";
    }

    @Override
    public String getDescription() {
        return "changes the node used to connect to and interact with the tangle";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        String nodeAddress = par[0];
        int mwm = Integer.parseInt(par[1]);
        println("connected to '"+nodeAddress+"', using minWeightMagnitude = " + mwm);
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        String nodeAddress = (String)parMap.get("node_address");
        int mwm = (int)parMap.get("min_weight_magnitude");

        if(mwm != TangleAPI.getInstance().getMWM())
            return new ResponseError("minWeightMagnitude change detected ("+TangleAPI.getInstance().getMWM()+" -> "+mwm+"); you cannot switch between testnet and mainnet yet");

        String protocol = nodeAddress.split("://")[0];
        String hostname = nodeAddress.split("://")[1].split(":")[0];
        String port = nodeAddress.split("://")[1].split(":")[1];

        TangleAPI.changeNode(protocol, hostname, port, mwm, Configs.getInstance().isLocalPowEnabled());

        return new ResponseSuccess();
    }
}
