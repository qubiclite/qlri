package commands.param.validators;

import commands.param.ParameterValidator;
import org.json.JSONArray;
import org.json.JSONException;

public class JSONArrayValidator extends ParameterValidator {

    @Override
    public String validate(String par) {
        try {
            new JSONArray(par);
            return null;
        } catch (JSONException e) {
            return "not a json array";
        }
    }

    @Override
    public Object convertParToObject(String par) {
        return new JSONArray(par);
    }

    @Override
    public String toString() {
        return "JSONArray";
    }

    @Override
    public String genJSValidation() {
        return "_ParameterValidator.validate_array("+getJSONKey()+", '"+getJSONKey()+"');";
    }

    @Override
    public String genPHPValidation() {
        return "$this->validate_array($"+getJSONKey()+", '$"+getJSONKey()+"');";
    }

    @Override
    public String getJSType() {
        return "array";
    }
}
