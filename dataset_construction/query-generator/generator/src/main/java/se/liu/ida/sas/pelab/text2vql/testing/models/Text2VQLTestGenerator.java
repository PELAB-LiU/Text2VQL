package se.liu.ida.sas.pelab.text2vql.testing.models;

import com.google.inject.Inject;
import se.liu.ida.sas.pelab.text2vql.ResourcesHelper;
import tools.refinery.generator.ModelGenerator;
import tools.refinery.generator.ModelGeneratorFactory;
import tools.refinery.generator.ProblemLoader;
import tools.refinery.generator.standalone.StandaloneRefinery;
import tools.refinery.language.model.problem.Problem;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Text2VQLTestGenerator {
	@Inject
	private ProblemLoader loader;
	@Inject
	private ModelGeneratorFactory generatorFactory;
	public static void main(String[] args) throws IOException, URISyntaxException {
		var runner = StandaloneRefinery.getInjector().getInstance(Text2VQLTestGenerator.class);
		runner.run();

	}
	private void run() throws IOException, URISyntaxException {
		var seeded = loader.loadUri(ResourcesHelper.emfURI("railway/railway.problem.seeded"));
		run(seeded, 300, "model_sd_%d.xmi");

		var seedless = loader.loadUri(ResourcesHelper.emfURI("railway/railway.problem.seedless"));
		run(seedless, 300, "model_sl_%d.xmi");
	}
	private void run(Problem problem, int times, String name) throws IOException, URISyntaxException {
		var mapper = new Problem2Railway();

		var generator = generatorFactory.createGenerator(problem);

		generator.setRandomSeed(0);

		for(int i = 1; i <= times; i++){
			System.out.println("Generation started for model "+ i+ " of "+times);
			generator.generate();
			var root = mapper.toEMF(generator);
			mapper.save(root, String.format(name, i));
		}

	}

}
