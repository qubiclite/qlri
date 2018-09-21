package resp.general;

import org.json.JSONArray;

public class ResponseError extends ResponseAbstract {

    public ResponseError(String errorMsg) {
        super();
        obj.put("success", false);
        obj.put("error", errorMsg);
    }

    public ResponseError(Throwable t) {
        super();
        obj.put("success", false);
        obj.put("error", t.getClass().getName() + ": " + t.getMessage());
        obj.put("stacktrace", genStackTraceJSONArray(t));
    }

    private static JSONArray genStackTraceJSONArray(Throwable t) {
        JSONArray stackTraceJSONArray = new JSONArray();
        for(StackTraceElement st : t.getStackTrace())
            stackTraceJSONArray.put( st.getClassName() + " ["+st.getLineNumber()+"]: " + st.getMethodName()+"()");
        return stackTraceJSONArray;
    }

    public String getError() {
        return obj.getString("error");
    }
}
