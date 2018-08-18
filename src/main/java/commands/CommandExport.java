package commands;

import oracle.OracleWriter;
import qubic.QubicWriter;
import resp.ResponseExport;
import resp.general.ResponseAbstract;
import resp.general.ResponseError;
import resp.general.ResponseSuccess;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import iam.IAMWriter;
import main.Persistence;

import java.util.List;
import java.util.Map;

public class CommandExport extends Command {

    public static final CommandExport instance = new CommandExport();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(81, 81).setName("id").setDescription("id of the entity to export")
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "export";
    }

    @Override
    public String getAlias() {
        return "ex";
    }

    @Override
    public String getDescription() {
        return "transforms an entity (iam stream, qubic or oracle) into a string that can be imported again";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        ResponseExport responseExport = (ResponseExport)response;
        println("export string: " + responseExport.getExport());
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String id = (String)parMap.get("id");

        List<IAMWriter> iamStreams = persistence.findAllIAMStreamsWithHandle(id);
        List<OracleWriter> oracleWriters = persistence.findAllOracleWritersWithHandle(id);
        List<QubicWriter> qubicWriters = persistence.findAllQubicWritersWithHandle(id);

        String prefix = null;
        IAMWriter exportIAM = null;

        if(iamStreams.size() > 0) {
            prefix = "i";
            exportIAM = iamStreams.get(0);
        }

        if(oracleWriters.size() > 0) {
            OracleWriter oracleWriter = oracleWriters.get(0);
            prefix = "o_" + oracleWriter.getQubicReader().getID();
            exportIAM = oracleWriter.getIAMWriter();
        }

        if(qubicWriters.size() > 0) {
            prefix = "q";
            exportIAM = qubicWriters.get(0).getIAMWriter();
        }

        if(exportIAM == null)
            return new ResponseError("could not find entity with id '"+id+"'");

        return new ResponseExport(prefix, exportIAM);
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {
        return new ResponseExport("q", new IAMWriter());
    }
}
