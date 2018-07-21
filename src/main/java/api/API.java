package api;

import api.resp.general.ResponseAbstract;
import api.resp.general.ResponseError;
import commands.Command;
import commands.param.CallValidator;
import io.undertow.server.HttpHandler;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import main.Main;
import main.Persistence;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;
import sun.nio.ch.ChannelInputStream;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static io.undertow.Handlers.path;

public class API {

    private static final int MAX_BODY_LENGTH = 10000;
    private Undertow undertow;
    private final Persistence persistence;

    public API(Persistence persistence, String host, int port) {

        this.persistence = persistence;

        if(host == null)
            try {
                host = InetAddress.getLocalHost().getHostAddress();
            } catch (UnknownHostException e) {
                e.printStackTrace();
                return;
            }

        Main.println("starting api listener for '"+host+':'+port+"'");

        // TODO credentials
        undertow = Undertow.builder().addHttpListener(port, host).setHandler(path().addPrefixPath("/", new HttpHandler() {

            @Override
            public void handleRequest(final HttpServerExchange exchange) throws Exception {

                if (exchange.isInIoThread()) {
                    exchange.dispatch(this);
                    return;
                }

                ChannelInputStream channelInputStream = new ChannelInputStream(exchange.getRequestChannel());
                String request = IOUtils.toString(channelInputStream, StandardCharsets.UTF_8);

                if(request.length() == 0) {
                    sendFile(exchange);
                } else {
                    long timeStarted = System.currentTimeMillis();
                    ResponseAbstract response = processRawRequest(request, exchange);
                    String responseString = response.toJSON().put("duration", System.currentTimeMillis()-timeStarted).toString();
                    exchange.getResponseSender().send(responseString);
                }

            }
        })).build();

        try {
            undertow.start();
        } catch (RuntimeException e) {
            Main.err("starting api failed" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void sendFile(HttpServerExchange exchange) throws IOException {

        String uri = exchange.getRequestURI();

        if(uri.equals("/")) uri = "/index.html";

        String contentType = "plain";

        if(uri.endsWith(".png")) contentType = "image/png";
        else if(uri.endsWith(".ico")) contentType = "image/x-icon";
        else if(uri.endsWith(".html")) contentType = "text/html";
        else if(uri.endsWith(".css")) contentType = "text/css";
        else if(uri.endsWith(".js")) contentType = "text/javascript";

        RandomAccessFile file;

        try {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            file = new RandomAccessFile("qlweb"+uri, "r");
        } catch (FileNotFoundException e) {
            exchange.getResponseSender().send("file not found: " + uri);
            return;
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);

        FileChannel inChannel = file.getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        buffer.load();
        exchange.getResponseSender().send(buffer);
    }

    private ResponseAbstract processRawRequest(String body, HttpServerExchange exchange) {

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

        // TODO validate version
        if (!exchange.getRequestHeaders().contains("X-QLITE-API-Version"))
            return new ResponseError("no api version declared in request");

        if (body.length() > MAX_BODY_LENGTH)
            return new ResponseError("request exceeds max length of "+MAX_BODY_LENGTH+" characters");

        JSONObject obj;

        try {
            obj = new JSONObject(body);
        } catch (JSONException e) {
            return new ResponseError("could not parse request to json object");
        }

        return processRequest(obj);
    }

    private ResponseAbstract processRequest(JSONObject request) {

        if(!request.has("command"))
            return new ResponseError("no command declared");

        String commandString = request.getString("command");

        Command command = Command.findCommand(commandString);

        if(command == null)
            return new ResponseError("unknown command: '"+commandString+"'");

        if(!command.isRemotelyAvailable())
            return new ResponseError("command '"+commandString+"' is not available via api, please call it directly from the terminal instead");

        String validationError;
        try {
            validationError = command.getCallValidator().validate(request);
        } catch (Throwable t) {
            validationError = t.getClass().getName() + ": " + t.getMessage();
        }

        if(validationError != null)
            return new ResponseError(validationError);

        Map<String, Object> parMap = genParMapFromJSON(command.getCallValidator(), request);
        return command.perform(persistence, parMap);
    }

    private Map<String, Object> genParMapFromJSON(CallValidator cv, JSONObject request) {
        Map<String, Object> parMap = new HashMap<>();
        for(String key : request.keySet()) {
            parMap.put(key.toLowerCase().replace(' ', '_'), request.get(key));
        }
        return cv.prepareParMap(parMap);
    }
}
