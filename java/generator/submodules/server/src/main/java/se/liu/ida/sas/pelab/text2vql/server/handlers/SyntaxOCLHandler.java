package se.liu.ida.sas.pelab.text2vql.server.handlers;

import com.sun.net.httpserver.HttpHandler;

import se.liu.ida.sas.pelab.text2vql.ocl.StatelessSyntaxCheckPivotOCL;
import se.liu.ida.sas.pelab.text2vql.server.SyntaxCheckRequest;
import se.liu.ida.sas.pelab.text2vql.server.util.EMFPackageManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ocl.xtext.essentialocl.EssentialOCLStandaloneSetup;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;

public class SyntaxOCLHandler implements HttpHandler{
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        SyntaxCheckRequest request = objectMapper.readValue(exchange.getRequestBody(), SyntaxCheckRequest.class);
        
        try{
        
        File metamodel = new File(request.wd(), request.metamodel());
        Resource meta = EMFPackageManager.INSTANCE.loadMetamodelToGlobalPackageRegistry(metamodel, EMFPackageManager.INSTANCE.resourceSet);
        
        //StatelessSyntaxCheckOCL checker = new StatelessSyntaxCheckOCL(){};
        StatelessSyntaxCheckPivotOCL checker = new StatelessSyntaxCheckPivotOCL(){};


        String response = objectMapper.writeValueAsString(checker.check(request.query(), meta));
        System.out.println(response);
        exchange.sendResponseHeaders(200, response.getBytes().length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }} catch(Exception e){
            System.out.println(request.query());
            e.printStackTrace(System.out);
            throw e;
        }
    }
    static {
        EssentialOCLStandaloneSetup.doSetup();
    }
}
