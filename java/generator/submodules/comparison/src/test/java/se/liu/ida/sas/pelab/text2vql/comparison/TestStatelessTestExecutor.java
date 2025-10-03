package se.liu.ida.sas.pelab.text2vql.comparison;

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
        TestCase test = new TestCase(null, truth, new Query[]{truth, faulty}, new Query[]{ocl1, ocl2}, new Query[]{});

        
        //TestCase test = new TestCase(null, truth, new Query[]{truth, faulty}, new Query[]{}, new Query[]{});


        StatelessTestExecutor executor = new StatelessTestExecutor(){};
        executor.serveTest(instances, test);
    }
    
}
