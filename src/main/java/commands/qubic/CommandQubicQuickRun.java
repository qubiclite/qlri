package commands.qubic;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.qubic.ResponseQubicQuickRun;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.FilePathValidator;
import main.Persistence;
import oracle.OracleManager;
import oracle.OracleWriter;
import qubic.QubicReader;
import qubic.QubicWriter;

import java.util.Map;

public class CommandQubicQuickRun extends Command {

    public static final CommandQubicQuickRun instance = new CommandQubicQuickRun();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new FilePathValidator().setName("qubic code").setExampleValue("../my_qubic.ql").setDescription("file containing the qubic code you want to quick run (absolute path or path relative to .jar file)"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "qubic_quick_run";
    }

    @Override
    public String getAlias() {
        return "qqr";
    }

    @Override
    public String getDescription() {
        return "runs a minimalistic qubic (will not be added to the persistence), automates the full qubic life cycle to allow the author to quickly test whether the code works as intended. only one oracle will be added to the assembly.";
    }

    @Override
    public boolean isRemotelyAvailable() {
        return false;
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        ResponseQubicQuickRun responseQQR = (ResponseQubicQuickRun) response;

        println("quick run started");
        println("qubic ID:  " + responseQQR.getQubicID());
        println("oracle ID: " + responseQQR.getOracleID());
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {


        String codePath = (String)parMap.get("qubic_code");

        String code = persistence.readFile(codePath);

        if(code == null)
            return new ResponseError("qubic test failed: could not read code from file");

        QubicWriter qw = new QubicWriter(30+(int)(System.currentTimeMillis()/1000), 20, 10, 10);
        qw.setCode(code);
        qw.publishQubicTx();

        OracleWriter ow = new OracleWriter(new QubicReader(qw.getID()));
        qw.addToAssembly(ow.getID());
        qw.publishAssemblyTx();

        new OracleManager(ow).start();

        return new ResponseQubicQuickRun(qw.getID(), ow.getID());
    }
}
