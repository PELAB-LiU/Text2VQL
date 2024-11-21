package se.liu.ida.sas.pelab.text2vql.refinery;

import com.google.inject.Inject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import se.liu.ida.sas.pelab.text2vql.refinery.templates.Ecore2Problem;
import se.liu.ida.sas.pelab.text2vql.refinery.templates.Result2VQL;
import se.liu.ida.sas.pelab.text2vql.refinery.templates.VQLTemplate;
import se.liu.ida.sas.pelab.text2vql.refinery.transformation.Problem2VQL;
import tools.refinery.generator.ModelGeneratorFactory;
import tools.refinery.generator.ProblemLoader;
import tools.refinery.generator.standalone.StandaloneRefinery;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class GeneratorMain {
    @Inject
    private ProblemLoader loader;
    @Inject
    private ModelGeneratorFactory generatorFactory;
    public static void main(String[] args) throws IOException, URISyntaxException {
        var runner = StandaloneRefinery.getInjector().getInstance(GeneratorMain.class);
        runner.run();
    }
    private void run() throws IOException, URISyntaxException {
        EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
        var resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
                "ecore", new EcoreResourceFactoryImpl());

        Resource meta = resourceSet.getResource(URI.createURI(
                        GeneratorMain.class.getClassLoader().getResource("railway/railway.ecore").toURI().toString()),
                true
        );
        EPackage epackage = (EPackage) meta.getContents().getFirst();



        var mapper = new Problem2VQL();
        String domain = Ecore2Problem.map2VQLProblem(epackage);

        System.out.println(domain);

        var problem = loader.loadString(domain);

        var generator = generatorFactory.createGenerator(problem);
        generator.setRandomSeed(System.nanoTime());

        var max = 1;
        for(int i = 1; i <= max; i++){
            System.out.println("Generation started for model "+ i+ " of "+max);
            generator.generate();
            var root = new Result2VQL(generator);
            System.out.println("Solution found.");
            System.out.println(root.regrow());
            //System.out.println(root);
            //mapper.save(root, "model"+i+".xmi");
        }
    }

}
