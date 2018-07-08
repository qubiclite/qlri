package commands.param.validators;

import commands.Command;
import commands.param.ParameterValidator;

public class ActionValidator extends ParameterValidator {

    @Override
    public String validate(String par) {

        return Command.findCommand(par) == null ? "there is no such command available: '"+par+"'" : null;
    }

    @Override
    public String toString() {
        return "COMMAND";
    }
}
