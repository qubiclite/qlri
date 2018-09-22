package api;

import commands.Command;
import commands.param.CallValidator;
import io.undertow.security.idm.Account;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import main.Main;
import main.Persistence;
import org.json.JSONException;
import org.json.JSONObject;
import resp.general.ResponseAbstract;
import resp.general.ResponseError;

import java.util.HashMap;
import java.util.Map;

class RequestProcessor {

    private static final int MAX_BODY_LENGTH = 10000;

    private final String body;
    private final HttpServerExchange exchange;
    private JSONObject request;
    private Command command;
    private final Account account;
    private final Persistence persistence;
    private final PrivilegedApiAccount defaultAccount = new PrivilegedApiAccount("", "");

    static ResponseAbstract process(Persistence persistence, String body, HttpServerExchange exchange) {
        return new RequestProcessor(persistence, body, exchange).processRawRequest();
    }

    private RequestProcessor(Persistence persistence, String body, HttpServerExchange exchange) {
        this.persistence = persistence;
        this.body = body;
        this.exchange = exchange;
        this.account = exchange.getSecurityContext() != null ? exchange.getSecurityContext().getAuthenticatedAccount() : defaultAccount;
    }

    private ResponseAbstract processRawRequest() {

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

        // TODO validate version
        if (!exchange.getRequestHeaders().contains("X-QLITE-API-Version"))
            return new ResponseError("no api version declared in request (use header: X-QLITE-API-Version)");

        if (!exchange.getRequestHeaders().getFirst("X-QLITE-API-Version").equals(Main.VERSION))
            return new ResponseError("api version does not match ql-node version (expected 'X-QLITE-API-Version: "+Main.VERSION+"')");

        if (body.length() > MAX_BODY_LENGTH)
            return new ResponseError("request exceeds max length of "+MAX_BODY_LENGTH+" characters");

        try {
            request = new JSONObject(body);
        } catch (JSONException e) {
            Main.println("failed parsing request to json: " + body);
            return new ResponseError("could not parse request to json object");
        }

        try {
            return processRequest();
        } catch (Throwable t) {
            return new ResponseError(t);
        }
    }

    private ResponseAbstract processRequest() throws IllegalAccessException {
        acceptValidCommand();
        validateCommandParameters();
        return executeCommand();
    }

    private ResponseAbstract executeCommand() {
        Map<String, Object> parMap = genParMapFromJSON(command.getCallValidator(), request);

        try {
            return command.perform(persistence, parMap);
        } catch (Throwable t) {
            return new ResponseError(t);
        }
    }

    private void acceptValidCommand() throws IllegalAccessException {
        if(!request.has("command"))
            throw new IllegalArgumentException("no command declared");

        String commandString = request.getString("command");
        command = Command.findCommand(commandString);

        if(command == null)
            throw new IllegalArgumentException("unknown command: '"+commandString+"'");

        if(!command.isRemotelyAvailable())
            throw new IllegalAccessException("command '"+commandString+"' is not available via api, please call it directly from the terminal instead");

        if(!account.getRoles().contains(command.getName()))
            throw new IllegalAccessException("user '"+account.getPrincipal().getName()+"' is not privileged to use command '"+commandString+"'");
    }

    private void validateCommandParameters() {

        String validationError;
        try {
            validationError = command.getCallValidator().validate(request);
        } catch (Throwable t) {
            validationError = t.getClass().getName() + ": " + t.getMessage();
        }

        if(validationError != null)
            throw  new IllegalArgumentException(validationError);
    }

    private Map<String, Object> genParMapFromJSON(CallValidator cv, JSONObject request) {
        Map<String, Object> parMap = new HashMap<>();
        for(String key : request.keySet()) {
            parMap.put(key.toLowerCase().replace(' ', '_'), request.get(key));
        }
        return cv.prepareParMap(parMap);
    }
}
