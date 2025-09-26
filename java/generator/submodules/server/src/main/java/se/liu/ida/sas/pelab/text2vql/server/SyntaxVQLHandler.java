package se.liu.ida.sas.pelab.text2vql.server;

import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import com.sun.net.httpserver.HttpExchange;

public class SyntaxVQLHandler implements HttpHandler{
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String response = "{\"syntax\": true, \"diagnostics\": \"All is well.\"}";
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }
}
