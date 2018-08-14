package commands.param.validators;

import commands.param.ParameterValidator;

import java.io.File;

public class FilePathValidator extends ParameterValidator {

    @Override
    public String validate(String par) {

        File f = new File(par);
        if (!f.exists())
            return "file '"+par+"' does not exist";
        if (f.isDirectory())
            return "'"+par+"' is a directory";
        return null;
    }

    @Override
    public Object convertParToObject(String par) {
        return par;
    }

    @Override
    public String toString() {
        return "FilePath";
    }

    @Override
    public String genJSValidation() {
        return "_ParameterValidator.validate_string("+getJSONKey()+", '"+getJSONKey()+"');";
    }
}
