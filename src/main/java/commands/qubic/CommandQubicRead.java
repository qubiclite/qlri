package commands.qubic;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import main.Main;
import main.Persistence;
import qubic.QubicReader;

import java.util.ArrayList;

public class CommandQubicRead extends Command {

    public static final CommandQubicRead instance = new CommandQubicRead();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(81, 81).setName("qubic id").setExampleValue("KSU9E…SZ999").setDescription("IAM stream identity of the qubic to read"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "qubic_read";
    }

    @Override
    public String getAlias() {
        return "qr";
    }

    @Override
    public String getDescription() {
        return "reads the metadata of any qubic, thus allows the user to analyze that qubic";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {

        String qubicId = par[1];
        QubicReader qr = new QubicReader(qubicId);

        println("ID:                  " + qr.getID());
        println("epoch duration:      " + qr.getEpochDuration() + "s (" + qr.getHashPeriodDuration() + "s + " + qr.getResultPeriodDuration() + "s)");
        println("execution start:     " + Main.DF.format(1000L * qr.getExecutionStart()) + " (" + qr.getExecutionStart() + ", "+(qr.getExecutionStart()-System.currentTimeMillis()/1000)+"s)");
        println("application address: " + qr.getApplicationAddress());

        long timeRunning = System.currentTimeMillis()/1000-qr.getExecutionStart();
        if(timeRunning > qr.getEpochDuration())
            println("last finished epoch: #" + (timeRunning / qr.getEpochDuration()));

        println("");
        println("code:");
        println("    " + qr.getCode());
        println("");
        ArrayList<String> assemblyList = qr.getAssemblyList();
        if(assemblyList != null) {
            println("assembly ("+assemblyList.size()+"):");
            for(String oracleID : assemblyList)
                println("  > " + oracleID);
        }
        else
            println("assembly:            not published yet");
    }
}