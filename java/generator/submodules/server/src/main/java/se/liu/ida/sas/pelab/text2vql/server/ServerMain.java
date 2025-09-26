package se.liu.ida.sas.pelab.text2vql.server;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        // Create an HTTP server on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(63028), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));
        
        // Define a context (URL path) and handler
        server.createContext("/vql", new SyntaxVQLHandler());

        // Start the server
        server.setExecutor(null); // creates a default executor
        System.out.println("Server started at http://localhost:63028");
        server.start();
    }
}
