package commands.param;

import org.apache.commons.lang3.StringUtils;

public class CallValidator {

    private ParameterValidator[] parameterValidators;

    public CallValidator(ParameterValidator[] parameterValidators) {
        this.parameterValidators = parameterValidators;
    }

    public String validate(String[] par) {

        for(int i = 0; i < parameterValidators.length; i++) {
            if(parameterValidators[i].isOptional())
                continue;
            String error = parameterValidators[i].validate(par.length > i+1 ? par[i+1] : "");
            if(error != null) return "parameter #"+(i+1)+" is incorrect: " + error;
        }

        return null;
    }

    @Override
    public String toString() {
        if(parameterValidators.length == 0)
            return "";

        String[] lines = new String[parameterValidators.length];
        for(int i = 0; i < parameterValidators.length; i++) {
            ParameterValidator p = parameterValidators[i];
            lines[i] = "(" + i + ") " + (StringUtils.rightPad(p.getName(), 30) + " … " + p.getDescription() + " {" + p + "}");
        }
        return StringUtils.join(lines, "\n");
    }

    public String buildExampleAllocation() {
        String[] exampleValues = new String[parameterValidators.length];
        for(int i = 0; i < parameterValidators.length; i++)
            exampleValues[i] = parameterValidators[i].getExampleValue();
        return StringUtils.join(exampleValues, " ");
    }
}