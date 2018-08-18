package commands.qubic;

import resp.general.ResponseAbstract;
import resp.general.ResponseError;
import resp.general.ResponseSuccess;
import resp.qubic.ResponseQubicQuickRun;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.FilePathValidator;
import main.Persistence;
import oracle.OracleManager;
import oracle.OracleWriter;
import qubic.EditableQubicSpecification;
import qubic.QubicReader;
import qubic.QubicWriter;
import tangle.TryteTool;

import java.util.Map;

public class CommandQubicQuickRun extends CommandQubicAbstract {

    public static final CommandQubicQuickRun instance = new CommandQubicQuickRun();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new FilePathValidator().setName("code").setExampleValue("../my_qubic.ql").setDescription("file containing the qubic code you want to quick run (absolute path or path relative to .jar file)"),
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
        return "Runs a minimalistic qubic, automates the full qubic life cycle to allow the author to quickly test whether the code works as intended. Only one oracle will be added to the assembly.";
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


        String codePath = (String)parMap.get("code");

        String code = persistence.readFile(codePath);

        if(code == null)
            return new ResponseError("qubic test failed: could not read code from file");

        QubicWriter qw = new QubicWriter();
        EditableQubicSpecification eqs = qw.getEditable();

        eqs.setExecutionStartToSecondsInFuture(20);
        eqs.setRuntimeLimit(10);
        eqs.setHashPeriodDuration(15);
        eqs.setResultPeriodDuration(5);
        eqs.setCode(code);

        OracleWriter ow = new OracleWriter(new QubicReader(qw.getID()));
        qw.getAssembly().add(ow.getID());
        qw.publishAssemblyTransaction();

        new OracleManager(ow).start();

        persistence.addOracleWriter(ow);
        persistence.addQubicWriter(qw);

        return new ResponseQubicQuickRun(qw.getID(), ow.getID());
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {
        return new ResponseQubicQuickRun(TryteTool.generateRandom(81), TryteTool.generateRandom(81));
    }
}
