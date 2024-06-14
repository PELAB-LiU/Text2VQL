package tools.refinery.generator.cli;

import com.google.inject.Inject;
import org.apache.log4j.ConsoleAppender;
import org.eclipse.emf.ecore.EObject;
import tools.refinery.generator.ModelGeneratorFactory;
import tools.refinery.generator.ProblemLoader;
import tools.refinery.generator.standalone.StandaloneRefinery;
import tools.refinery.language.model.problem.Assertion;
import tools.refinery.language.model.problem.NodeAssertionArgument;
import tools.refinery.language.resource.ProblemResource;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

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
		var mapper = new Problem2RAilway();
		var problem = loader.loadFile(new File(getClass()
				.getClassLoader().getResource("railway.problem").toURI()));

		var generator = generatorFactory.createGenerator(problem);
		generator.setRandomSeed(0);

		var max = 300;
		for(int i = 1; i <= max; i++){
			System.out.println("Generation started for model "+ i+ " of "+max);
			generator.generate();
			var root = mapper.toEMF(generator);
			mapper.save(root, "model"+i+".xmi");
		}
	}

}
