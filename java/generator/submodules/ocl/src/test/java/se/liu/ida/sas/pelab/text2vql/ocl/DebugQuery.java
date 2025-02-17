package se.liu.ida.sas.pelab.text2vql.ocl;

import org.eclipse.emf.ecore.EPackage;
import org.eclipse.ocl.OCL;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.expressions.OCLExpression;
import org.eclipse.ocl.helper.OCLHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;

public class DebugQuery {
    @Test
    public void debug(){
        String query = """
                Segment.allInstances()->collect(segment | segment.length>0)
                """;
/*
        EcoreEnvironmentFactory environmentFactory = new EcoreEnvironmentFactory(EPackage.Registry.INSTANCE);
        OCL ocl = OCL.newInstanceAbstract(environmentFactory);
        OCLHelper helper = ocl.createOCLHelper();
        helper.setContext(railway.getEClassifier("RailwayContainer"));
        OCLExpression expression = helper.createQuery(query);
        return ocl.createQuery(expression);
        Object result = query.evaluate(resource.getContents().getFirst());*/
    }
}
