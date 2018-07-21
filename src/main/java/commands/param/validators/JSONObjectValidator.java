package commands.param.validators;

import commands.param.ParameterValidator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONObjectValidator extends ParameterValidator {

    @Override
    public String validate(String par) {
        try {
            new JSONObject(par);
            return null;
        } catch (JSONException e) {
            return "not a json object";
        }
    }

    @Override
    public Object convertParToObject(String par) {
        return new JSONObject(par);
    }

    @Override
    public String toString() {
        return "JSON OBJECT";
    }
}
