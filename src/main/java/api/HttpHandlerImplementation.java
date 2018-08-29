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


    void processRequest(HttpServerExchange exchange, String request) {
        if(exchange.getRequestURI().replace("/", "").length() > 0)
            try {
                api.sendFile(exchange);
            } catch (IOException e) {
                e.printStackTrace();
            }
        else {
            long timeStarted = System.currentTimeMillis();
            ResponseAbstract response = api.processRawRequest(request, exchange);
            String responseString = response.toJSON().put("duration", System.currentTimeMillis()-timeStarted).toString();
            exchange.getResponseSender().send(responseString);
        }
    }
}
