package commands.param.validators;

import commands.param.ParameterValidator;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public class URLValidator extends ParameterValidator {

    @Override
    public String validate(String par) {

        if(!par.startsWith("https://") && !par.startsWith("http://"))
            return "url should start with either 'https://' or 'http://'";

        try {
            new URL(par);
            return null;
        } catch (MalformedURLException e) {
            return "url malformed: " + e.getMessage();
        }
    }

    @Override
    public Object convertParToObject(String par) {
        return par;
    }

    @Override
    public String toString() {
        return "URL";
    }

    @Override
    public String genJSValidation() {
        return "_ParameterValidator.validate_string("+getJSONKey()+", '"+getJSONKey()+"');";
    }

    @Override
    public String genPHPValidation() {
        return "$this->validate_string($"+getJSONKey()+", '$"+getJSONKey()+"');";
    }
}
