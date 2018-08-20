package commands;

import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import iam.IAMWriter;
import main.Persistence;
import oracle.OracleWriter;
import qubic.QubicWriter;
import resp.ResponseExport;
import resp.ResponseNodeInfo;
import resp.general.ResponseAbstract;
import resp.general.ResponseError;
import resp.general.ResponseSuccess;

import java.util.List;
import java.util.Map;

public class CommandNodeInfo extends Command {

    public static final CommandNodeInfo instance = new CommandNodeInfo();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{ });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "node_info";
    }

    @Override
    public String getAlias() {
        return "ni";
    }

    @Override
    public String getDescription() {
        return "Gives details about this ql-node.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        ResponseNodeInfo responseNodeInfo = (ResponseNodeInfo)response;
        println("VERSION:   " + responseNodeInfo.getVersion());
        println("IOTA NODE: " + responseNodeInfo.getIOTANode());
        println("TESTNET:   " + responseNodeInfo.isOnTestnet());
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        return new ResponseNodeInfo();
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {
        return new ResponseNodeInfo();
    }
}
