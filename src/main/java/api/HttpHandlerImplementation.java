package api;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import main.Main;

public abstract class HttpHandlerImplementation implements HttpHandler {

    @Override
    public void handleRequest(final HttpServerExchange exchange) {

        // TODO credentials

        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Headers"), "Origin, X-Requested-With, Content-Type, Accept, X-QLITE-API-Version");

        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        exchange.getRequestReceiver().receiveFullBytes((exchange2, data) -> {
                String request = new String(data);
                processRequest(exchange, request);
            }, (exchange2, exception) -> {
                Main.println("api failed reading request body");
                exception.printStackTrace();
            }
        );
    }

    abstract void processRequest(HttpServerExchange exchange, String request);
}
