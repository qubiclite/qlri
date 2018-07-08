package commands.param.validators;

import commands.param.ParameterValidator;

public class TryteValidator extends ParameterValidator {

    private final int minLength;
    private final int maxLength;

    public TryteValidator(int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public String validate(String par) {

        par = par.toUpperCase();

        char[] trytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZ9".toCharArray();

        for(int i = 0; i < par.length(); i++) {
            char c = par.charAt(i);
            if((c < 'A' || c > 'Z') && c != '9')
                return "'"+par+"' is not a tryte sequence: contains illegal character '"+c+"'";
        }

        if(par.length() < minLength || par.length() > maxLength)
            return "length of "+buildRequiredLengthString()+" trytes required, but length of '"+par+"' is " + par.length();
        return null;
    }

    private String buildRequiredLengthString() {
        if(minLength == maxLength)
            return "" + minLength;
        return minLength + "-" + maxLength;
    }

    @Override
    public String toString() {
        return buildRequiredLengthString() + " TRYTES";
    }
}
