package commands.param.validators;

import commands.param.ParameterValidator;

public class IntegerValidator extends ParameterValidator {

    private final int min;
    private final int max;

    public IntegerValidator(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public String validate(String par) {
        try {
            int i = Integer.valueOf(par);
            if(i > max)
                return "'"+par+"' is greater than allowed maximum: " + max;
            if(i < min)
                return "'"+par+"' is smaller than allowed minimum: " + min;
            return null;
        } catch (NumberFormatException e) {
            return "integer required but received '"+par+"'";
        }
    }

    @Override
    public Object convertParToObject(String par) {
        return Integer.parseInt(par);
    }

    @Override
    public String toString() {
        return "INTEGER ["+min+","+max+"]";
    }
}
