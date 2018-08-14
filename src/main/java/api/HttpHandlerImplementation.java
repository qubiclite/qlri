package api;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import org.apache.commons.io.IOUtils;
import sun.nio.ch.ChannelInputStream;

import java.nio.charset.StandardCharsets;

public abstract class HttpHandlerImplementation implements HttpHandler {

    @Override
    public void handleRequest(final HttpServerExchange exchange) throws Exception {

        // TODO credentials

        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Headers"), "Origin, X-Requested-With, Content-Type, Accept, X-QLITE-API-Version");

        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        ChannelInputStream channelInputStream = new ChannelInputStream(exchange.getRequestChannel());
        String request = IOUtils.toString(channelInputStream, StandardCharsets.UTF_8);

        processRequest(exchange, request);

    }

    abstract void processRequest(HttpServerExchange exchange, String request);
}
