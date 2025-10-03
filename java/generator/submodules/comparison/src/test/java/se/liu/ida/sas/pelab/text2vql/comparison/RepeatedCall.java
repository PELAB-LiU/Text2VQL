package se.liu.ida.sas.pelab.text2vql.comparison;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.Query;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;
import org.junit.jupiter.api.Test;

public class RepeatedCall {
    @Test
    public void test() throws ParserException{
        OCL ocl = OCL.newInstance();
        OCLHelper helper = ocl.createOCLHelper();
        helper.setContext(EcorePackage.Literals.ECLASS);
        OCLExpression expression = helper.createQuery("EClass.allInstances()->select(c | c.abstract = false)");

        

        EObject model1 = EcoreUtil.copy(EcorePackage.eINSTANCE);
        EObject model2 = EcoreUtil.copy(EcorePackage.eINSTANCE);

        Query reuse = ocl.createQuery(expression);
        System.out.println("Reused query on first model:");
        System.out.println(reuse.evaluate(model1));
        System.out.println();
        System.out.println("Reused query on second model:");
        System.out.println(reuse.evaluate(model2)); // Yields the same result as reuse.evaluate(model1); Inconcistent with model2
        System.out.println();

        Query newQuery = ocl.createQuery(expression);
        System.out.println("Reused query on second model:");
        System.out.println(newQuery.evaluate(model2)); // Yields a result consistent with model2
        System.out.println();
    }
}
