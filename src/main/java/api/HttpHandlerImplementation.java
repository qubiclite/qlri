package api;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import main.Main;
import resp.general.ResponseAbstract;

import java.io.IOException;

public class HttpHandlerImplementation implements HttpHandler {

    private final API api;

    HttpHandlerImplementation(API api) {
        this.api = api;
    }

    @Override
    public void handleRequest(final HttpServerExchange exchange) {

        putResponseHeaders(exchange);

        if (exchange.isInIoThread()) {
            exchange.dispatch(this);
            return;
        }

        exchange.getRequestReceiver().receiveFullBytes((exchange2, data) -> {
                String body = new String(data);
                processRequest(exchange, body);
            }, (exchange2, exception) -> {
                Main.println("api failed reading request body");
                exception.printStackTrace();
            }
        );
    }

    private void putResponseHeaders(HttpServerExchange exchange) {
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Origin"), "*");
        exchange.getResponseHeaders().put(new HttpString("Access-Control-Allow-Headers"), "Origin, X-Requested-With, Content-Type, Accept, X-QLITE-API-Version");
    }

    private void processRequest(HttpServerExchange exchange, String body) {
        if(exchange.getRequestURI().replace("/", "").length() > 0)
            try {
                API.sendFile(exchange);
            } catch (IOException e) {
                e.printStackTrace();
            }
        else {
            long timeStarted = System.currentTimeMillis();
            ResponseAbstract response = RequestProcessor.process(api.getPersistence(), body, exchange);
            String responseString = response.toJSON().put("duration", System.currentTimeMillis()-timeStarted).toString();
            exchange.getResponseSender().send(responseString);
        }
    }
}
