package commands.param.validators;

import commands.param.ParameterValidator;

import java.util.regex.Pattern;

public class NodeAddressValidator extends ParameterValidator {

    private final static Pattern NODE_ADDRESS_PATTERN = Pattern.compile("^(http|https)://[a-z0-9.\\-]+:[0-9]{1,5}$");

    @Override
    public String validate(String par) {
        if(!NODE_ADDRESS_PATTERN.matcher(par).find())
            return "'"+par+"' is not a valid node address (correct format 'https://my.node.org:14265')";
        return null;
    }

    @Override
    public String toString() {
        return "NODE ADDRESS";
    }
}