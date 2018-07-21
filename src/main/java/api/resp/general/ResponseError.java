package api.resp.general;

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
    }

    public String getError() {
        return obj.getString("error");
    }
}
