package se.liu.ida.sas.pelab.text2vql.comparison;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.comparison.input.Query;
import se.liu.ida.sas.pelab.text2vql.comparison.jobs.JavaJob;

public class TestJavaJob {
    @Test
    public void test() throws Exception{
        var ecore = EcorePackage.eINSTANCE;
        //var ecore = PatternLanguagePackage.eINSTANCE;
        Resource metamodels = new ResourceImpl();
        metamodels.getContents().add(EcoreUtil.copy(EcorePackage.eINSTANCE));
        var job = new JavaJob(metamodels, new Query(0, "query", normalClassesRecord), null);
        job.configureInstanceModel(ecore);
        System.out.println(job.call());
    }
    
    
    
    
    public static String abstractClasses = """
public class Query {

    /**
     * Returns all non-abstract EClasses contained in the given EMF Resource.
     *
     * @param resource the EMF resource to search
     * @return list of non-abstract EClass instances
     */
    public List<EClass> query(Resource resource) {
        List<EClass> result = new ArrayList<>();

        if (resource == null) {
            return result;
        }

        // Traverse all contents of the resource
        for (EObject eObject : resource.getContents()) {
            collectEClasses(eObject, result);
        }

        return result;
    }

    /**
     * Recursively traverses EObjects to find EClasses.
     */
    private void collectEClasses(EObject eObject, List<EClass> result) {
        if (eObject instanceof EClass) {
            EClass eClass = (EClass) eObject;
            if (eClass.isAbstract()) {
                result.add(eClass);
            }
        }

        // Recurse into children
        for (EObject child : eObject.eContents()) {
            collectEClasses(child, result);
        }
    }
}      
            """;
    public static String normalClasses = """
public class Query {

    /**
     * Returns all non-abstract EClasses contained in the given EMF Resource.
     *
     * @param resource the EMF resource to search
     * @return list of non-abstract EClass instances
     */
    public List<EClass> query(Resource resource) {
        List<EClass> result = new ArrayList<>();

        if (resource == null) {
            return result;
        }

        // Traverse all contents of the resource
        for (EObject eObject : resource.getContents()) {
            collectEClasses(eObject, result);
        }

        return result;
    }

    /**
     * Recursively traverses EObjects to find EClasses.
     */
    private void collectEClasses(EObject eObject, List<EClass> result) {
        if (eObject instanceof EClass) {
            EClass eClass = (EClass) eObject;
            if (!eClass.isAbstract()) {
                result.add(eClass);
            }
        }

        // Recurse into children
        for (EObject child : eObject.eContents()) {
            collectEClasses(child, result);
        }
    }
}      
            """;
    public static String normalClassesRecord = """
public class Query {
    public static record Record(EClass eclass){};

    public List<Record> query(Resource resource) {
        List<Record> result = new ArrayList<>();

        if (resource == null) {
            return result;
        }

        // Traverse all contents of the resource
        for (EObject eObject : resource.getContents()) {
            collectEClasses(eObject, result);
        }

        return result;
    }

    /**
     * Recursively traverses EObjects to find EClasses.
     */
    private void collectEClasses(EObject eObject, List<Record> result) {
        if (eObject instanceof EClass) {
            EClass eClass = (EClass) eObject;
            if (!eClass.isAbstract()) {
                result.add(new Record(eClass));
            }
        }

        // Recurse into children
        for (EObject child : eObject.eContents()) {
            collectEClasses(child, result);
        }
    }
}    
            """;
}
