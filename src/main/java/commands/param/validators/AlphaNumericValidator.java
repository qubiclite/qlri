package commands.param.validators;

import commands.param.ParameterValidator;
import org.apache.commons.lang3.StringUtils;

public class AlphaNumericValidator extends ParameterValidator {

    @Override
    public String validate(String par) {

        if(!StringUtils.isAlphanumeric(par))
            return "parameter contains non-alphanumeric characters";

        return null;
    }

    @Override
    public Object convertParToObject(String par) {
        return par;
    }

    @Override
    public String toString() {
        return "Alphanumeric";
    }

    @Override
    public String genJSValidation() {
        return "_ParameterValidator.validate_alphanumeric("+getJSONKey()+", '"+getJSONKey()+"');";
    }
}
