package se.liu.ida.sas.pelab.text2vql.comparison;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.OCLJob;

public class TestOCLJob {
    public static String normalClasses = """
            EClass.allInstances()->select(c | c.abstract = false)
            """;
    public static String abstractClasses = """
            EClass.allInstances()->select(c | c.abstract = true)
            """;
    public static String normalClassesTuple = """
            EClass.allInstances()->select(c | c.abstract = false)->collect(c | Tuple{node = c})
            """;
    public static String abstractClassesTuple = """
            EClass.allInstances()->select(c | c.abstract = true)->collect(c | Tuple{node = c})
            """;
    @Test
    public void testJob() throws Exception{
        var ecore = EcorePackage.eINSTANCE;
        //var ecore = PatternLanguagePackage.eINSTANCE;
        Resource metamodels = new ResourceImpl();
        metamodels.getContents().add(EcoreUtil.copy(EcorePackage.eINSTANCE));
        var job = new OCLJob(metamodels, new Query(0, "", normalClassesTuple));
        job.configureInstanceModel(ecore);
        System.out.println(job.call());
    }    
}
