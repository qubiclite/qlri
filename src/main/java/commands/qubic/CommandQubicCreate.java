package commands.qubic;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.FilePathValidator;
import commands.param.validators.IntegerValidator;
import main.Persistence;
import qubic.QubicWriter;

public class CommandQubicCreate extends Command {

    public static final CommandQubicCreate instance = new CommandQubicCreate();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new IntegerValidator(1, Integer.MAX_VALUE).setName("execution start").setExampleValue("300").setDescription("amount of seconds until (or unix timestamp for) end of assembly phase and start of execution phase"),
        new IntegerValidator(1, Integer.MAX_VALUE).setName("hash period duration").setExampleValue("30").setDescription("amount of seconds each hash period (first part of the epoch) lasts"),
        new IntegerValidator(1, Integer.MAX_VALUE).setName("result period duration").setExampleValue("30").setDescription("amount of seconds each result period (second part of the epoch) lasts"),
        new IntegerValidator(1, Integer.MAX_VALUE).setName("run time limit").setExampleValue("10").setDescription("maximum amount of seconds the QLVM is allowed to run per epoch before aborting (to prevent endless loops)"),
        new FilePathValidator().setName("qubic code").setExampleValue("../my_qubic.ql").setDescription("file containing the qubic code (absolute path or path relative to .jar file)"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
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
    public void perform(Persistence persistence, String[] par) {

        int executionStart = par.length > 1 ? Integer.parseInt(par[1]) : 120;
        if(executionStart < System.currentTimeMillis()/1000)
            executionStart += System.currentTimeMillis()/1000;

        int hashPeriodDuration = Integer.parseInt(par[2]);
        int resultPeriodDuration = Integer.parseInt(par[3]);
        int runTimeLimit = Integer.parseInt(par[4]);

        if(runTimeLimit > hashPeriodDuration * 0.7) {
            println("the run time limit should be smaller than 70% of the hash period duration");
            return;
        }

        String code_path = par[5];
        String code = persistence.readFile(code_path);
        if(code == null) {
            println("qubic creation failed: could not read code from file");
            return;
        }

        println("creating qubic ...");

        QubicWriter qw = new QubicWriter(executionStart, hashPeriodDuration, resultPeriodDuration, runTimeLimit);
        qw.setCode(code);
        persistence.addQubicWriter(qw);
        qw.publishQubicTx();

        println("created qubic with id:  '" + qw.getID() + "'");
        println("qubic transaction:      '" + qw.getQubicTxHash() + "'");
        println("execution starts in      " + (int)(qw.getExecutionStart()-System.currentTimeMillis()/1000) + " seconds");
    }
}
