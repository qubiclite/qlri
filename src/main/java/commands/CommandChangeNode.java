package commands;

import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.IntegerValidator;
import commands.param.validators.NodeAddressValidator;
import main.Persistence;
import tangle.TangleAPI;

public class CommandChangeNode extends Command {

    public static final CommandChangeNode instance = new CommandChangeNode();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new NodeAddressValidator().setName("node address").setExampleValue("https://node.example.org:14265").setDescription("address to any node api (mainnet or testnet, depending on what ql-nodes you want to connect to)"),
        new IntegerValidator(9, 14).setName("min weight magnitude").setExampleValue("14").setDescription("always use 14, use 9 only when connecting to a testnet node"),
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
    public void perform(Persistence persistence, String[] par) {

        String address = par[1];
        String protocol = address.split("://")[0];
        String hostname = address.split("://")[1].split(":")[0];
        String port = address.split("://")[1].split(":")[1];

        int mwm = par.length <= 2 ? 14 : Integer.parseInt(par[2]);

        println("connecting to '" + protocol + "://" + hostname + ":" + port+"', using minWeightMagnitude = " + mwm);

        TangleAPI.changeNode(protocol, hostname, port, mwm);
    }
}
