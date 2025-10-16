package se.liu.ida.sas.pelab.text2vql.server.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpHandler;

import se.liu.ida.sas.pelab.text2vql.server.SyntaxCheckRequest;
import se.liu.ida.sas.pelab.text2vql.server.util.EMFPackageManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import com.sun.net.httpserver.HttpExchange;

import se.liu.ida.sas.pelab.text2vql.vql.StatelessVQLSyntaxCheck;


public class SyntaxVQLHandler implements HttpHandler{

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ResourceSet resourceSet = EMFPackageManager.INSTANCE.resourceSet;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try{
            SyntaxCheckRequest request = objectMapper.readValue(exchange.getRequestBody(), SyntaxCheckRequest.class);

            System.out.println("Hello VQL handler. 7");
            File metamodel = new File(request.wd(), request.metamodel());
            Resource meta = EMFPackageManager.INSTANCE.loadMetamodelToGlobalPackageRegistry(metamodel, resourceSet);

            var checker = new StatelessVQLSyntaxCheck(){};
            String response = objectMapper.writeValueAsString(checker.check(meta, request.query()));
    
            System.out.println("Response: "+response);
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch (Exception e){
            e.printStackTrace(System.out);
            throw e;
        }
    }
}
