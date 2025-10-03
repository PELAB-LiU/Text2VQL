package se.liu.ida.sas.pelab.text2vql.comparison;

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
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

        Query truth = new Query("normalClasses", TestVQLJob.normalClasses);
        Query faulty = new Query("abstractClasses", TestVQLJob.abstractClasses);

        Query ocl1 = new Query("", TestOCLJob.abstractClasses);
        Query ocl2 = new Query("", TestOCLJob.normalClassesTuple);

        Query java1 = new Query("query", TestJavaJob.normalClasses);
        Query java2 = new Query("query", TestJavaJob.abstractClasses); 
        TestCase test = new TestCase(null, truth, new Query[]{truth, faulty}, new Query[]{ocl1, ocl2}, new Query[]{java1, java2});

        
        //TestCase test = new TestCase(null, truth, new Query[]{truth, faulty}, new Query[]{}, new Query[]{});


        StatelessTestExecutor executor = new StatelessTestExecutor(){};
        try {
            executor.serveTest(instances, test, null);
        } catch (MalformedURLException | ClassNotFoundException | NoSuchMethodException | SecurityException
                | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | InterruptedException | ExecutionException e) {// Happy Friday: There are a few failure points...
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
}
