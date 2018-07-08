package commands;

import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.ActionValidator;
import main.Persistence;
import org.apache.commons.lang3.StringUtils;

public class CommandHelp extends Command {

    public static final CommandHelp instance = new CommandHelp();


    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new ActionValidator().setName("command").setDescription("command to display specific details to").setExampleValue("qubic_list").makeOptional(),
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getAlias() {
        return "?";
    }

    @Override
    public String getDescription() {
        return "helps the user by listing all available commands and providing details to any specific command";
    }

    @Override
    public void perform(Persistence persistence, String[] par) {

        String command = par.length == 1 ? null : par[1];

        if(command == null) {

            println("--- commands ---");
            println("");
            for(Command a : Command.COMMANDS)
                println(StringUtils.rightPad(a.getName(), 30) + " alias: " + a.getAlias());
            println("");
            println("for more information to a specific command, use: 'help [command]'");
        } else {

            Command a = Command.findCommand(command);
            if(a == null) {
                println("unknown command '"+command+"', maybe try 'help'");
                return;
            }

            println("--- details: "+a.getName()+" ("+a.getAlias()+") ---");
            println("");
            println("DESCRIPTION:");
            println("");
            println(a.getDescription());
            println("");
            println("PARAMETERS:");
            println("");
            println(a.getCallValidator().toString());
            println("");
            println("EXAMPLE USE:");
            println("");
            println("$ " + a.getName() + " " + a.getCallValidator().buildExampleAllocation());
        }
    }
}
