package se.liu.ida.sas.pelab.text2vql.comparison;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.comparison.jobs.VQLJob;
import se.liu.ida.sas.pelab.text2vql.vql.StatelessVQLSyntaxCheck;

public class TestVQLJob {
    public static String normalClasses = """
            pattern normalClasses(cls : EClassifier){
                EClass.abstract(cls, false);
            }
            """;
    public static String abstractClasses = """
            pattern abstractClasses(cls : EClassifier){
                EClass.abstract(cls, true);
            }
            """;
    @Test
    public void testJob() throws Exception{
        StatelessVQLSyntaxCheck.init();

        var ecore = EcorePackage.eINSTANCE;
        var job = new VQLJob(new ResourceImpl(), "normalClasses", normalClasses);
        job.configureInstanceModel(ecore);
        job.call();
    }    
}
