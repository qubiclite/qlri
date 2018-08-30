package commands;

import resp.general.ResponseAbstract;
import resp.general.ResponseError;
import resp.general.ResponseSuccess;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.IntegerValidator;
import commands.param.validators.NodeAddressValidator;
import main.Configs;
import main.Persistence;
import tangle.NodeAddress;
import tangle.TangleAPI;

import java.util.Map;

public class CommandChangeNode extends Command {

    public static final CommandChangeNode instance = new CommandChangeNode();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new NodeAddressValidator().setName("node address").setExampleValue("https://node.example.org:14265").setDescription("address of any IOTA full node api (mainnet or testnet, depending on which ql-nodes you want to be able to interact with)"),
        new IntegerValidator(9, 14).setName("mwm").setExampleValue("14").setDescription("min weight magnitude: use 9 when connecting to a testnet node, otherwise use 14").makeOptional(14),
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
        return "Changes the IOTA full node used to interact with the tangle.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        String nodeAddress = par[0];
        int mwm = Integer.parseInt(par[1]);
        println("connected to '"+nodeAddress+"', using minWeightMagnitude = " + mwm);
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        NodeAddress nodeAddress = new NodeAddress((String)parMap.get("node_address"));
        int mwm = (int)parMap.get("min_weight_magnitude");

        if(mwm != TangleAPI.getInstance().getMWM())
            return new ResponseError("minWeightMagnitude change detected ("+TangleAPI.getInstance().getMWM()+" -> "+mwm+"); you cannot switch between testnet and mainnet yet");

        TangleAPI.changeNode(nodeAddress, mwm, Configs.getInstance().isLocalPowEnabled());

        return new ResponseSuccess();
    }
}
