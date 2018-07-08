package commands.qubic;

import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.TryteValidator;
import constants.TangleJSONConstants;
import main.Persistence;
import org.json.JSONObject;
import qubic.QubicWriter;

public class CommandQubicListApplications extends Command {

    public static final CommandQubicListApplications instance = new CommandQubicListApplications();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new TryteValidator(1, 81).setName("qubic handle").setExampleValue("G9").setDescription("the qubic from which you want to read and list all applications"),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "qubic_list_applications";
    }

    @Override
    public String getAlias() {
        return "qla";
    }

    @Override
    public String getDescription() {
        return "lists all incoming oracle applications for a specific qubic, basis for '" + CommandQubicAssemblyAdd.instance.getName() + "'";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {

        String handle = par[1];
        QubicWriter qw = persistence.findQubicWriterByHandle(handle);

        if (qw != null) {
            qw.fetchApplications();
            JSONObject[] applications = qw.getApplications();
            println("found " + applications.length + " application(s):");
            for (JSONObject application : applications) {
                String oracleID = escapeInput(application.getString(TangleJSONConstants.ORACLE_ID));
                String oracleName = escapeInput(application.getString(TangleJSONConstants.ORACLE_NAME));

                println("  > " + oracleID + " \"" + oracleName + "\"");
            }
        }
    }

    private static String escapeInput(String raw) {
        return raw.replace("\\", "\\\\");
    }
}
