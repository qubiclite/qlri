package api;

import io.undertow.server.HttpHandler;
import main.Configs;
import io.undertow.Undertow;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.*;
import main.Main;
import main.Persistence;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import static io.undertow.Handlers.path;

public class API {

    public static final String QLWEB_PATH = "qlweb/qlweb-"+Main.QLWEB_VERSION;
    private final Persistence persistence;

    public API(Persistence persistence, String host, int port)  throws UnknownHostException {

        if(!new File(QLWEB_PATH).exists())
            QLWebInstaller.installQLWeb();

        this.persistence = persistence;

        if(host == null)
            host = InetAddress.getLocalHost().getHostAddress();

        try {
            buildUndertow(host, port).start();
            Main.println("starting api listener: '"+host+':'+port+"'");
            Main.println("qlweb is available on: '"+host+':'+port+"/index.html'");
        } catch (RuntimeException e) {
            Main.err("starting api failed" + e.getMessage());
            e.printStackTrace();
        }
    }

    private Undertow buildUndertow(String host, int port) {
        HttpHandler httpHandler = createHttpHandler();
        return Undertow.builder()
                .addHttpListener(port, host)
                .setHandler(path().addPrefixPath("/", httpHandler)).build();
    }

    private HttpHandler createHttpHandler() {
        HttpHandler httpHandler = new HttpHandlerImplementation(this);
        MapIdentityManager identityManager = new MapIdentityManager(Configs.getInstance().getAccounts());
        return SecurityInitialHandlerFactory.create(httpHandler, identityManager);
    }

    static void sendFile(HttpServerExchange exchange) throws IOException {

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

    public Persistence getPersistence() {
        return persistence;
    }
}
