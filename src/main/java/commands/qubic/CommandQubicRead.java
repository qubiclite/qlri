package commands.qubic;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import api.resp.qubic.ResponseQubicRead;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import exceptions.InvalidQubicTransactionException;
import main.Main;
import main.Persistence;
import org.json.JSONArray;
import qubic.QubicReader;

import java.util.Map;

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
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        ResponseQubicRead r = (ResponseQubicRead)response;

        long epochDuration = r.getHashPeriodDuration() + r.getResultPeriodDuration();
        int lastCompletedEpoch = lastCompletedEpoch(r.getExecutionStart(), epochDuration);

        println("ID:                   " + r.getID());
        println("version:              " + r.getVersion());
        println("application address:  " + r.getApplicationAddress());
        println("epoch duration:       " + epochDuration + "s (" + r.getHashPeriodDuration() + "s + " + r.getResultPeriodDuration() + "s)");
        println("execution start:      " + Main.DF.format(1000L * r.getExecutionStart()) + " (" + r.getExecutionStart() + ", "+(r.getExecutionStart()-System.currentTimeMillis()/1000)+"s)");

        long timeRunning = System.currentTimeMillis()/1000-r.getExecutionStart();
        if(timeRunning > epochDuration)
            println("last completed epoch: #" + lastCompletedEpoch);

        println("");
        println("code:");
        println("    " + r.getCode());
        println("");
        JSONArray assemblyList = r.getAssemblyList();
        if(assemblyList != null) {
            println("assembly ("+assemblyList.length()+"):");
            for(int i = 0; i < assemblyList.length(); i++)
                println("  > " + assemblyList.get(i));
        }
        else
            println("assembly:            not published yet");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String qubicID = (String)parMap.get("qubic_id");
        try {
            QubicReader qr = new QubicReader(qubicID);
            return new ResponseQubicRead(qr);
        } catch (InvalidQubicTransactionException e) {
            return new ResponseError(e);
        }
    }

    private int lastCompletedEpoch(long executionStart, long epochDuration) {
        long timeRunning = System.currentTimeMillis() / 1000L - executionStart;
        return (int)Math.floor((double)(timeRunning / epochDuration)) - 1;
    }
}