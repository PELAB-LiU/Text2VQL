package se.liu.ida.sas.pelab.text2vql.refinery.mains;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import com.google.inject.Inject;

import se.liu.ida.sas.pelab.text2vql.refinery.Text2VQLTestGenerator;
import se.liu.ida.sas.pelab.text2vql.refinery.domain.Problem2Railway;
import se.liu.ida.sas.pelab.text2vql.refinery.domain.Problem2RailwayV2;
import se.liu.ida.sas.pelab.text2vql.refinery.util.Text2VQLProjectStructure;
import se.liu.ida.sas.pelab.text2vql.utilities.ResourcesHelper;
import tools.refinery.generator.ModelGeneratorFactory;
import tools.refinery.generator.ProblemLoader;
import tools.refinery.generator.standalone.StandaloneRefinery;
import tools.refinery.language.model.problem.Problem;

public class RailwayMain {
    @Inject
	private ProblemLoader loader;
	@Inject
	private ModelGeneratorFactory generatorFactory;
	public static void main(String[] args) throws IOException, URISyntaxException {
		var runner = StandaloneRefinery.getInjector().getInstance(RailwayMain.class);
		runner.run();

	}
	private void run() throws IOException, URISyntaxException {
        File dir = Text2VQLProjectStructure.createIn("results/testmodels", "railway");
        
		var seeded = loader.loadUri(ResourcesHelper.emfURI("railway.seeded.problem"));
		run(seeded, 300, dir, "model_sd_%d.xmi");

		var seedless = loader.loadUri(ResourcesHelper.emfURI("railway.seedless.problem"));
		run(seedless, 300, dir, "model_sl_%d.xmi");
	}
	private void run(Problem problem, int times, File dir, String name) throws IOException, URISyntaxException {
        
		var mapper = new Problem2RailwayV2();//Problem2Railway();

		var generator = generatorFactory.createGenerator(problem);

		generator.setRandomSeed(0);

		for(int i = 1; i <= times; i++){
			System.out.println("Generation started for model "+ i+ " of "+times);
			generator.generate();
			var root = mapper.toEMF(generator);
			mapper.save(root, dir, String.format(name, i));
		}

	}
}
