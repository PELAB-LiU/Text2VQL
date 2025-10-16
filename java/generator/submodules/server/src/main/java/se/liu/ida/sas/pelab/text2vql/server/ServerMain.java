package se.liu.ida.sas.pelab.text2vql.server;

import com.sun.net.httpserver.HttpServer;

import se.liu.ida.sas.pelab.text2vql.server.handlers.EvaluationHandler;
import se.liu.ida.sas.pelab.text2vql.server.handlers.SyntaxJavaHandler;
import se.liu.ida.sas.pelab.text2vql.server.handlers.SyntaxOCLHandler;
import se.liu.ida.sas.pelab.text2vql.server.handlers.SyntaxVQLHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class ServerMain {
    public static void main(String[] args) throws IOException {
        // Create an HTTP server on port 8000
        HttpServer server = HttpServer.create(new InetSocketAddress(63028), 0);
        server.setExecutor(Executors.newFixedThreadPool(10));
        
        // Define a context (URL path) and handler
        server.createContext("/vql", new SyntaxVQLHandler());
        server.createContext("/java", new SyntaxJavaHandler());
        server.createContext("/ocl", new SyntaxOCLHandler());
        server.createContext("/eval", new EvaluationHandler());

        // Start the server
        server.setExecutor(null); // creates a default executor
        System.out.println("Server started at http://localhost:63028");
        server.start();
    }
}
