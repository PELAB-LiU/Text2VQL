package se.liu.ida.sas.pelab.text2vql.epsilon;

import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.epl.execute.PatternMatch;
import org.eclipse.epsilon.epl.execute.model.PatternMatchModel;
import se.liu.ida.sas.pelab.text2vql.utilities.RailwayLoader;
import se.liu.ida.sas.pelab.text2vql.utilities.ResourcesHelper;

public class ValidateWithEOL {
    public static void main(String[] args) throws Exception {
        EplModule module = new EplModule();
        module.parse(ResourcesHelper.javaURI("sample.epl"));

        InMemoryEmfModel model = new InMemoryEmfModel(RailwayLoader.loadRailway());
        module.getContext().getModelRepository().addModel(model);
        PatternMatchModel resultModel = (PatternMatchModel) module.execute();
        System.out.println("Matches:");
        for (PatternMatch match : resultModel.getMatches()) {
            System.out.println("\t" + match.toString());
        }
        System.out.println(resultModel.getClass());
    }
}
