package se.liu.ida.sas.pelab.text2vql.server.handlers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpHandler;

import se.liu.ida.sas.pelab.text2vql.server.SyntaxCheckRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;

import se.liu.ida.sas.pelab.text2vql.vql.StatelessVQLSyntaxCheck;


public class SyntaxVQLHandler implements HttpHandler{

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                String response = "Method Not Allowed";
                System.out.println("NON POST Request.");
                exchange.sendResponseHeaders(405, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
                return;
            }
        try{
            SyntaxCheckRequest request = objectMapper.readValue(exchange.getRequestBody(), SyntaxCheckRequest.class);

            System.out.println("Hello VQL handler. 7");
            //System.out.println(request);

            var checker = new StatelessVQLSyntaxCheck(){};
            String response = checker.check(new File(request.wd(), request.metamodel()), request.query()).toString();

    
            System.out.println("Response: "+response);
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (Exception e){
            System.out.println(e);
            e.printStackTrace();
            throw e;
        }
    }
    public SyntaxVQLHandler(){
        StatelessVQLSyntaxCheck.init();
    }
}
