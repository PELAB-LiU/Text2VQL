package se.liu.ida.sas.pelab.text2vql.comparison;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternLanguagePackage;
import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;
import se.liu.ida.sas.pelab.text2vql.comparison.input.TestCase;
import se.liu.ida.sas.pelab.text2vql.vql.StatelessVQLSyntaxCheck;

public class TestStatelessTestExecutor {

    @Test
    public void testServeTest() throws InterruptedException, ExecutionException{
        StatelessVQLSyntaxCheck.init();

        List<EObject> instances = new ArrayList<EObject>();
        
        instances.add(PatternLanguagePackage.eINSTANCE);
        instances.add(EcorePackage.eINSTANCE);

        Query truth = new Query(0, "normalClasses", TestVQLJob.normalClasses);

        Query vql1 = new Query(1, "normalClasses", TestVQLJob.normalClasses);
        Query vql2 = new Query(2, "abstractClasses", TestVQLJob.abstractClasses);

        Query ocl1 = new Query(3, "", TestOCLJob.abstractClasses);
        Query ocl2 = new Query(4, "", TestOCLJob.normalClassesTuple);

        Query java1 = new Query(5, "query", TestJavaJob.normalClasses);
        Query java2 = new Query(6, "query", TestJavaJob.abstractClasses); 
        TestCase test = new TestCase(truth, new Query[]{vql1, vql2}, new Query[]{ocl1, ocl2}, new Query[]{java1, java2});

        
        //TestCase test = new TestCase(null, truth, new Query[]{truth, faulty}, new Query[]{}, new Query[]{});


        StatelessTestExecutor executor = new StatelessTestExecutor(){};
        try {
            System.out.println(executor.serveTest(new ResourceImpl(), instances, test, null));
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | InterruptedException | ExecutionException e) {// Happy Friday: There are a few failure points...
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
