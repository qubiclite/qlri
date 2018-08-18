package commands.param.validators;

import commands.param.ParameterValidator;
import tangle.TryteTool;

public class TryteValidator extends ParameterValidator {

    private final int minLength;
    private final int maxLength;

    public TryteValidator(int minLength, int maxLength) {
        this.minLength = minLength;
        this.maxLength = maxLength;
        setExampleValue(TryteTool.generateRandom(Math.min(minLength+1, maxLength)));
    }

    @Override
    public String validate(String par) {

        par = par.toUpperCase();

        if(!TryteTool.isTryteSequence(par))
            return "'"+par+"' is not a tryte sequence: contains illegal character";

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
    public Object convertParToObject(String par) {
        return par;
    }

    @Override
    public String toString() {
        return "Trytes{"+buildRequiredLengthString()+"}";
    }

    @Override
    public String genJSValidation() {
        return "_ParameterValidator.validate_tryte_sequence("+getJSONKey()+", '"+getJSONKey()+"', "+minLength+", "+maxLength+");";
    }

    @Override
    public String genPHPValidation() {
        return "$this->validate_tryte_sequence($"+getJSONKey()+", '$"+getJSONKey()+"', "+minLength+", "+maxLength+");";
    }
}
