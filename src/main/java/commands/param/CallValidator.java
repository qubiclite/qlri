package commands.param;

import main.Main;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

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
            if(error != null) return "parameter #"+(i+1)+" is invalid: " + error;
        }

        return null;
    }

    public String validate(JSONObject jsonObject) {

        for(int i = 0; i < parameterValidators.length; i++) {
            if(parameterValidators[i].isOptional())
                continue;
            String requiredParName = parameterValidators[i].getName();
            if(!jsonObject.has(requiredParName) && !jsonObject.has( parameterValidators[i].getJSONKey()))
                return "missing parameter '"+requiredParName+"'";
            String error = parameterValidators[i].validate(jsonObject.get(requiredParName).toString());
            if(error != null) return "parameter '"+requiredParName+"' is invalid: " + error;
        }

        return null;
    }


    public Map<String, Object> genParMap(String[] par) {
        Map<String, Object> parMap = new HashMap();


        for(int i = 0; i < parameterValidators.length; i++) {

            ParameterValidator pv = parameterValidators[i];

            Object val;
            if(pv.isOptional() && (par.length <= i+2 || par[i+1] == null))
                val = pv.getDefaultValue();
            else
                val = pv.convertParToObject(par[i+1]);

            parMap.put(pv.getJSONKey(), val);
        }

        return parMap;
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

    public JSONObject buildExampleJSONRequest() {
        JSONObject o = new JSONObject();
        for(ParameterValidator pv : parameterValidators)
            o.put(pv.getJSONKey(), pv.getExampleValue());
        return o;
    }

    public Map<String, Object> prepareParMap(Map<String, Object> parMap) {
        for(ParameterValidator pv : parameterValidators) {
            String key = pv.getJSONKey();
            if(pv.isOptional() && !parMap.containsKey(key))
                parMap.put(key, pv.getDefaultValue());
        }
        return parMap;
    }

    public String genDoc() {

        StringJoiner sj = new StringJoiner("\n * ");

        for(ParameterValidator pv : parameterValidators) {
            String type = pv.toString();
            String paramName = pv.isOptional() ? '[' + pv.getJSONKey() + ']' : pv.getJSONKey();
            String description = pv.getDescription();

            sj.add("@apiParam {" + type + "} " + paramName + " " + description);
        }

        return sj.toString();
    }

    public ParameterValidator[] getParameterValidators() {
        return parameterValidators;
    }
}