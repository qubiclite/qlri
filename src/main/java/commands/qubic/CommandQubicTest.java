package commands.qubic;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.FilePathValidator;
import commands.param.validators.IntegerValidator;
import main.Persistence;
import qlvm.QLVM;

public class CommandQubicTest extends Command {

    public static final CommandQubicTest instance = new CommandQubicTest();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new FilePathValidator().setName("qubic code").setExampleValue("../my_qubic.ql").setDescription("file containing the qubic code you want to test (absolute path or path relative to .jar file)"),
        new IntegerValidator(0, Integer.MAX_VALUE).setName("epoch index").setExampleValue("3").setDescription("initializes the run time variable 'epoch' to simulate a running qubic").makeOptional()
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "qubic_test";
    }

    @Override
    public String getAlias() {
        return "qt";
    }

    @Override
    public String getDescription() {
        return "runs ql code locally (instead of over the tangle) to allow the author to quickly test whether it works as intended";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {

        String code_path = par[1];
        int epoch = par.length < 3 ? 0 : Integer.parseInt(par[2]);
        String code = persistence.readFile(code_path);

        if(code == null) {
            println("qubic test failed: could not read code from file");
            return;
        }

        println("TEST RUN RESULT (for epoch #"+epoch+"):\n");
        long start = System.currentTimeMillis();
        println(QLVM.testRun(code, epoch));
        long end = System.currentTimeMillis();
        println("\ntest run took "+(end-start)+"ms");
    }
}
