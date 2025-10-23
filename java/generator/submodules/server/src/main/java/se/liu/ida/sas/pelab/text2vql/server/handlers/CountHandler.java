package se.liu.ida.sas.pelab.text2vql.server.handlers;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

import com.fasterxml.jackson.databind.ObjectMapper;

import se.liu.ida.sas.pelab.text2vql.comparison.StatelessCounter;
import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;
import se.liu.ida.sas.pelab.text2vql.server.util.EMFPackageManager;
import se.liu.ida.sas.pelab.text2vql.server.util.XMIModelCache;

public class CountHandler implements HttpHandler{
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final XMIModelCache cache = new XMIModelCache();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try{
            EvaluationTask request = objectMapper.readValue(exchange.getRequestBody(), EvaluationTask.class);

            
            
            File metamodel = new File(request.wd(), request.metamodel());
            Resource meta = EMFPackageManager.INSTANCE.loadMetamodelToGlobalPackageRegistry(metamodel, EMFPackageManager.INSTANCE.resourceSet);

            List<EObject> models = cache.getModels(request.getModels(), meta,-1);
            
            StatelessCounter tester = new StatelessCounter(){};
            Map<String, Integer> result = tester.serveTest(meta, models, request.query());

            String response = objectMapper.writeValueAsString(result);
            
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        } catch(Exception e){
            e.printStackTrace(System.out);
            //throw e;
        }
    }

    public static record EvaluationTask(String wd, String metamodel, String models, Query query){
        public Path getWd(){
            return Path.of(wd);
        }
        public File getMetamodel(){
            return new File(wd, metamodel);
        }
        public File getModels(){
            return new File(wd, models);
        }
    };
}
