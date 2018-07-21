package commands.qubic;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.qubic.ResponseQubicCreate;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.FilePathValidator;
import commands.param.validators.IntegerValidator;
import commands.param.validators.StringValidator;
import main.Persistence;
import qubic.QubicWriter;

import java.util.Map;

public class CommandQubicCreate extends Command {

    public static final CommandQubicCreate instance = new CommandQubicCreate();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new IntegerValidator(1, Integer.MAX_VALUE).setName("execution start"),
            new IntegerValidator(1, Integer.MAX_VALUE).setName("hash period duration"),
            new IntegerValidator(1, Integer.MAX_VALUE).setName("result period duration"),
            new IntegerValidator(1, Integer.MAX_VALUE).setName("runtime limit"),
            new StringValidator().setName("code"),
    });

    private static final CallValidator CV_TERMINAL = new CallValidator(new ParameterValidator[]{
            new IntegerValidator(1, Integer.MAX_VALUE).setName("execution start").setExampleValue("300").setDescription("amount of seconds until (or unix timestamp for) end of assembly phase and start of execution phase"),
            new IntegerValidator(1, Integer.MAX_VALUE).setName("hash period duration").setExampleValue("30").setDescription("amount of seconds each hash period (first part of the epoch) lasts"),
            new IntegerValidator(1, Integer.MAX_VALUE).setName("result period duration").setExampleValue("30").setDescription("amount of seconds each result period (second part of the epoch) lasts"),
            new IntegerValidator(1, Integer.MAX_VALUE).setName("runtime limit").setExampleValue("10").setDescription("maximum amount of seconds the QLVM is allowed to run per epoch before aborting (to prevent endless loops)"),
            new FilePathValidator().setName("code path").setExampleValue("../my_qubic.ql").setDescription("file containing the qubic code (absolute path or path relative to .jar file)"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public CallValidator getCallValidatorForTerminal() {
        return CV_TERMINAL;
    }

    @Override
    public String getName() {
        return "qubic_create";
    }

    @Override
    public String getAlias() {
        return "qc";
    }

    @Override
    public String getDescription() {
        return "creates a new qubic and stores it in the persistence. life cycle will not be automized: do the assembly transaction manually";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        ResponseQubicCreate responseQC = (ResponseQubicCreate) response;

        QubicWriter qw = persistence.findQubicWriterByHandle(responseQC.getQubicID());

        println("created qubic with id:  '" + responseQC.getQubicID() + "'");
        println("qubic transaction:      '" + qw.getQubicTxHash() + "'");
        println("execution starts in      " + (int)(qw.getExecutionStart()-System.currentTimeMillis()/1000) + " seconds");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        int executionStart = (int)parMap.get("execution_start");
        int hashPeriodDuration = (int)parMap.get("hash_period_duration");
        int resultPeriodDuration = (int)parMap.get("result_period_duration");
        int runTimeLimit = (int)parMap.get("runtime_limit");
        String codePath = (String)parMap.get("code_path");

        final long currentTimeMillis = System.currentTimeMillis()/1000;

        if(executionStart < currentTimeMillis && executionStart > 3600 * 24 * 365 * 20)
            return new ResponseError("the timestamp '"+executionStart+"' has already passed, please recheck parameter 'execution start'");
        if(executionStart > 3600 * 24 * 365)
            return new ResponseError("execution would start in more than a year, please recheck parameter 'execution start'");

        if(runTimeLimit > hashPeriodDuration * 0.8)
            return new ResponseError("the run time limit should be smaller than 80% of the hash period duration");
        if(runTimeLimit > hashPeriodDuration - 5)
            return new ResponseError("the run time limit should be smaller than the hash period duration minus 5 seconds");

        String code = codePath == null ? (String)parMap.get("code") : persistence.readFile(codePath);
        if(code == null)
            return new ResponseError("qubic test failed: could not read code from file");

        if (executionStart < currentTimeMillis)
            executionStart += currentTimeMillis;

        QubicWriter qw = new QubicWriter(executionStart, hashPeriodDuration, resultPeriodDuration, runTimeLimit);
        qw.setCode(code);
        persistence.addQubicWriter(qw);
        qw.publishQubicTx();

        return new ResponseQubicCreate(qw.getID());
    }
}
