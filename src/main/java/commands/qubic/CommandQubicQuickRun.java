package commands.qubic;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.FilePathValidator;
import main.Persistence;
import oracle.OracleManager;
import oracle.OracleWriter;
import qubic.QubicReader;
import qubic.QubicWriter;

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
    public void perform(Persistence persistence, String[] par) {

        String code_path = par[1];
        String code = persistence.readFile(code_path);

        QubicWriter qw = new QubicWriter(30+(int)(System.currentTimeMillis()/1000), 20, 10, 10);
        println("created qubic: " + qw.getID());
        qw.setCode(code);
        qw.publishQubicTx();

        OracleWriter ow = new OracleWriter(new QubicReader(qw.getID()));
        println("created oracle: " + ow.getID());
        qw.addToAssembly(ow.getID());
        qw.publishAssemblyTx();

        new OracleManager(ow).start();
    }
}
