package commands.param.validators;

import commands.param.ParameterValidator;

public class StringValidator extends ParameterValidator {

    @Override
    public String validate(String par) {
        return null;
    }

    @Override
    public Object convertParToObject(String par) {
        return par;
    }

    @Override
    public String toString() {
        return "STRING";
    }
}
