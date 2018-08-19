package api;

import resp.general.ResponseAbstract;
import resp.general.ResponseError;
import commands.Command;
import commands.param.CallValidator;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import main.Main;
import main.Persistence;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.Map;

import static io.undertow.Handlers.path;

public class API {

    private static final String QLWEB_VERSION = "0.4.1";
    private static final String QLWEB_DOWNLOAD_URL = "https://github.com/qubiclite/qlweb/archive/v"+QLWEB_VERSION+".zip";
    public static final String QLWEB_PATH = "qlweb/qlweb-"+QLWEB_VERSION;
    private static final int MAX_BODY_LENGTH = 10000;

    private final Undertow undertowAPI;
    private final Persistence persistence;

    public API(Persistence persistence, String host, int port)  throws UnknownHostException {

        if(!new File(QLWEB_PATH).exists())
            installQLWeb();

        this.persistence = persistence;

        if(host == null)
            host = InetAddress.getLocalHost().getHostAddress();

        Main.println("starting api listener: '"+host+':'+port+"'");
        Main.println("qlweb is available on: '"+host+':'+port+"/index.html'");

        undertowAPI = Undertow.builder().addHttpListener(port, host).setHandler(path().addPrefixPath("/", new HttpHandlerImplementation(){
            @Override
            void processRequest(HttpServerExchange exchange, String request) {
                if(exchange.getRequestURI().replace("/", "").length() > 0)
                    try {
                        sendFile(exchange);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                else {
                    long timeStarted = System.currentTimeMillis();
                    ResponseAbstract response = processRawRequest(request, exchange);
                    String responseString = response.toJSON().put("duration", System.currentTimeMillis()-timeStarted).toString();
                    exchange.getResponseSender().send(responseString);
                }
            }
        })).build();

        try {
            undertowAPI.start();
        } catch (RuntimeException e) {
            Main.err("starting api failed" + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void sendFile(HttpServerExchange exchange) throws IOException {

        String uri = exchange.getRequestURI();

        if(uri.endsWith("/")) uri += "index.html";

        String contentType = "plain";

        if(uri.endsWith(".png")) contentType = "image/png";
        else if(uri.endsWith(".ico")) contentType = "image/x-icon";
        else if(uri.endsWith(".html")) contentType = "text/html";
        else if(uri.endsWith(".css")) contentType = "text/css";
        else if(uri.endsWith(".js")) contentType = "text/javascript";

        RandomAccessFile file;

        try {
            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
            file = new RandomAccessFile(QLWEB_PATH +uri, "r");
        } catch (FileNotFoundException e) {
            exchange.getResponseSender().send("file not found: " + uri);
            return;
        }

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, contentType);

        FileChannel inChannel = file.getChannel();
        MappedByteBuffer buffer = inChannel.map(FileChannel.MapMode.READ_ONLY, 0, inChannel.size());
        buffer.load();
        exchange.getResponseSender().send(buffer);
        file.close();
    }

    private ResponseAbstract processRawRequest(String body, HttpServerExchange exchange) {

        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");

        // TODO validate version
        if (!exchange.getRequestHeaders().contains("X-QLITE-API-Version"))
            return new ResponseError("no api version declared in request (use header: X-QLITE-API-Version)");

        if (!exchange.getRequestHeaders().getFirst("X-QLITE-API-Version").equals(Main.VERSION))
            return new ResponseError("api version does not match ql-node version (expected 'X-QLITE-API-Version: "+Main.VERSION+"')");

        if (body.length() > MAX_BODY_LENGTH)
            return new ResponseError("request exceeds max length of "+MAX_BODY_LENGTH+" characters");

        JSONObject obj;

        try {
            obj = new JSONObject(body);
        } catch (JSONException e) {
            Main.println("failed parsing request to json: " + body);
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

        try {
            return command.perform(persistence, parMap);
        } catch (Throwable t) {
            return new ResponseError(t);
        }
    }

    private Map<String, Object> genParMapFromJSON(CallValidator cv, JSONObject request) {
        Map<String, Object> parMap = new HashMap<>();
        for(String key : request.keySet()) {
            parMap.put(key.toLowerCase().replace(' ', '_'), request.get(key));
        }
        return cv.prepareParMap(parMap);
    }

    private static void installQLWeb() {

        Main.println("installing qlweb ...");
        try {
            URL url = new URL(QLWEB_DOWNLOAD_URL);
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream("qlweb.zip");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            ZipFile zipFile = new ZipFile("qlweb.zip");
            zipFile.extractAll("./qlweb/" );
            new File("qlweb.zip").delete();
            Main.println("qlweb installation complete");

        } catch (IOException | ZipException e) {
            Main.println("qlweb installation failed");
            e.printStackTrace();
        }
    }
}
