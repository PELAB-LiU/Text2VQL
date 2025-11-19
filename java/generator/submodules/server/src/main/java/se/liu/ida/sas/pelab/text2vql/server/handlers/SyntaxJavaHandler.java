package se.liu.ida.sas.pelab.text2vql.server.handlers;

import com.sun.net.httpserver.HttpHandler;

import se.liu.ida.sas.pelab.text2vql.server.SyntaxCheckRequest;
import se.liu.ida.sas.pelab.text2vql.server.util.EMFPackageManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.emf.ecore.resource.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

import se.liu.ida.sas.pelab.text2vql.java.StatelessSyntaxCheckJava;

public class SyntaxJavaHandler implements HttpHandler{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try{
            SyntaxCheckRequest request = objectMapper.readValue(exchange.getRequestBody(), SyntaxCheckRequest.class);


        File metamodel = new File(request.wd(), request.metamodel());
        Resource meta = EMFPackageManager.INSTANCE.loadMetamodelToGlobalPackageRegistry(metamodel, EMFPackageManager.INSTANCE.resourceSet);
        File jar = new File(request.wd(), request.jar());
        
        StatelessSyntaxCheckJava checker = new StatelessSyntaxCheckJava(){};
        String response = objectMapper.writeValueAsString(checker.check(request.query(), meta, jar));

        System.out.println(response);
        exchange.sendResponseHeaders(200, response.getBytes().length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    } catch(Exception e){
        e.printStackTrace(System.out);
        throw e;
    }
    }
}
