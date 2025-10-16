package se.liu.ida.sas.pelab.text2vql.server;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

import se.liu.ida.sas.pelab.text2vql.server.util.XMIModelCache;

public class EvaluationTest {
    @Test
    public void loadModels() throws IOException{
        var xmis = new XMIModelCache();
        var models = xmis.getModels(
            new File("/workspaces/Text2VQL/results/testmodels"),
            new File("/workspaces/Text2VQL/dataset_construction/test_metamodel/railway.ecore"),
            3);
        assertNotNull(models);
    }
}
