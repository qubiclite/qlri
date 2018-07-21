package commands.qubic;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.qubic.ResponseQubicTest;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.FilePathValidator;
import commands.param.validators.IntegerValidator;
import commands.param.validators.StringValidator;
import main.Persistence;
import qlvm.QLVM;

import java.util.Map;

public class CommandQubicTest extends Command {

    public static final CommandQubicTest instance = new CommandQubicTest();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new StringValidator().setName("code"),
            new IntegerValidator(0, Integer.MAX_VALUE).setName("epoch index").makeOptional(1)
    });

    private static final CallValidator CV_TERMINAL = new CallValidator(new ParameterValidator[]{
        new FilePathValidator().setName("code path").setExampleValue("../my_qubic.ql").setDescription("file containing the qubic code you want to test (absolute path or path relative to .jar file)"),
        new IntegerValidator(0, Integer.MAX_VALUE).setName("epoch index").setExampleValue("3").setDescription("initializes the run time variable 'epoch' to simulate a running qubic").makeOptional(1)
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
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        int epoch = par.length < 3 ? 0 : Integer.parseInt(par[2]);
        ResponseQubicTest responseQT = ((ResponseQubicTest) response);

        println("TEST RUN RESULT (for epoch #"+epoch+"):\n");
        println(responseQT.getResult());
        println("\ntest run took "+(responseQT.getRuntime())+"ms");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        int epoch = (int)parMap.get("epoch_index");
        String codePath = (String)parMap.get("code_path");

        String code = codePath == null ? (String)parMap.get("code") : persistence.readFile(codePath);
        if(code == null)
            return new ResponseError("qubic test failed: could not read code from file");

        long start = System.currentTimeMillis();
        String result = QLVM.testRun(code, epoch);
        long runtime = System.currentTimeMillis()-start;

        return new ResponseQubicTest(result, runtime);
    }
}
