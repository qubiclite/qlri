package commands.param.validators;

import commands.Command;
import commands.param.ParameterValidator;

public class ActionValidator extends ParameterValidator {

    @Override
    public String validate(String par) {

        return Command.findCommand(par) == null ? "there is no such command available: '"+par+"'" : null;
    }

    @Override
    public Object convertParToObject(String par) {
        return par;
    }

    @Override
    public String toString() {
        return "Command";
    }

    @Override
    public String genJSValidation() {
        return "_ParameterValidator.validate_string("+getJSONKey()+", '"+getJSONKey()+"');";
    }
}
