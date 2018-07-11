package commands;

import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.IntegerValidator;
import commands.param.validators.TryteValidator;
import main.Persistence;
import oracle.QuorumBasedResult;
import qlvm.InterQubicResultFetcher;
import qubic.QubicReader;

public class CommandFetchEpoch extends Command {

    public static final CommandFetchEpoch instance = new CommandFetchEpoch();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
            new TryteValidator(81, 81).setName("qubic id").setExampleValue("KSU9E…SZ999").setDescription("IAM stream identity of the qubic to fetch from"),
            new IntegerValidator(0, Integer.MAX_VALUE).setName("epoch index").setExampleValue("4").setDescription("epoch to fetch"),
            new IntegerValidator(0, Integer.MAX_VALUE).setName("epoch index max").setExampleValue("7").setDescription("will fetch all epochs from 'epoch index' to 'epoch index max' if this parameter is set").makeOptional()
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "fetch_epoch";
    }

    @Override
    public String getAlias() {
        return "fe";
    }

    @Override
    public String getDescription() {
        return "determines the quorum based result (which can be considered the consensus) of any qubic at any epoch";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {

        String qubicId = par[1];
        int epoch_min = Integer.parseInt(par[2]);
        int epoch_max = par.length > 3 ? Integer.parseInt(par[3]) : epoch_min;

        int lastCompletedEpoch = new QubicReader(qubicId).lastCompletedEpoch();

        if(epoch_max > lastCompletedEpoch) {
            println("WARNING: epoch #"+(lastCompletedEpoch+1)+" is still running, only results for epoch <= "+lastCompletedEpoch+" are available.\n");
            epoch_max = lastCompletedEpoch;
        }

        for(int epoch = epoch_min; epoch <= epoch_max; epoch++) {

            println("--- EPOCH #" + epoch + " ---");

            QuorumBasedResult qbr = InterQubicResultFetcher.fetchResult(qubicId, epoch);

            println("RESULT: " + qbr.getResult());
            double percentage = Math.round(1000 * qbr.getQuorum() / qbr.getQuorumMax()) / 10;
            println("QUORUM: " + qbr.getQuorum() + " / " + qbr.getQuorumMax() + " ("+(percentage)+"%)");
            println("");
        }
    }
}
