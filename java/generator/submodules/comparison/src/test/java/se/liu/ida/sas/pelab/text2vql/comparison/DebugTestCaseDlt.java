package se.liu.ida.sas.pelab.text2vql.comparison;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;
import se.liu.ida.sas.pelab.text2vql.comparison.input.TestCase;
import se.liu.ida.sas.pelab.text2vql.utilities.Text2VQLProjectStructure;
import se.liu.ida.sas.pelab.text2vql.vql.StatelessVQLSyntaxCheck;

public class DebugTestCaseDlt {

    @Test
    public void evaluate() throws InterruptedException, ExecutionException{
        StatelessVQLSyntaxCheck.init();

        Query truthq = new Query(0, "channelInKafkaNetworkWithoutOrderer", truth);

        Query refq = new Query(1, "channelInKafkaNetworkWithoutOrderer", reference);

        TestCase test = new TestCase(truthq, new Query[]{}, new Query[]{}, new Query[]{refq});

        
        StatelessTestExecutor executor = new StatelessTestExecutor(){};
        Resource metamodel = loadMetamodel("dataset_construction/test_metamodel/dlt.ecore").eResource();
        List<EObject> instances = new ArrayList<>();
        instances.add(loadModel((EPackage) metamodel.getContents().getFirst(), "results/testmodels/dlt/model_sd_1.xmi"));
        assertEquals(1, metamodel.getContents().size());


        try {
            System.out.println(executor.serveTest(metamodel, instances, test, Text2VQLProjectStructure.find("dataset_construction/test_metamodel/dlt.jar")));
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | InterruptedException | ExecutionException e) {// Happy Friday: There are a few failure points...
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    private String truth = """
pattern channelInKafkaNetworkWithoutOrderer(channel: Channel){
	KafkaFabricNetwork.channels(_, channel);
	neg Channel.orderedBy(channel, _);
}
""";
    private String reference = """
public class Query { 
    public List<Channel> channelInKafkaNetworkWithoutOrderer(Resource resource){        List<Channel> result = new ArrayList<>();
        for (EObject root : resource.getContents()) {
            collect(root, result);
        }
        return result;
    }

    private void collect(EObject eObject, List<Channel> result) {
        if (eObject instanceof KafkaFabricNetwork kfn) {
            for (Channel c : kfn.getChannels()) {
                if (c.getOrderedBy() == null) {
                    result.add(c);
                }
            }
        }
        for (EObject child : eObject.eContents()) {
            collect(child, result);
        }
    }
}
    """;

    private EObject loadMetamodel(String metapath){
        final ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("xmi", new XMIResourceFactoryImpl());
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("ecore", new XMIResourceFactoryImpl());
        Resource res = rs.getResource(URI.createFileURI(
            Text2VQLProjectStructure.find(metapath).getAbsolutePath()), true);
        
        EPackage ePackage = (EPackage) res.getContents().getFirst();
        EPackage.Registry.INSTANCE.putIfAbsent(ePackage.getNsURI(), ePackage);

        return ePackage;
    }

    private EObject loadModel(EPackage metamodel, String modelpath){
        final ResourceSet rs = new ResourceSetImpl();
        rs.getResourceFactoryRegistry().getExtensionToFactoryMap()
            .put("xmi", new XMIResourceFactoryImpl());
        rs.getPackageRegistry().put(metamodel.getNsURI(), metamodel);
        Resource res = rs.getResource(URI.createFileURI(
            Text2VQLProjectStructure.find(modelpath).getAbsolutePath()), true);
        return res.getContents().getFirst();
    }
    
}
