package se.liu.ida.sas.pelab.text2vql.epsilon;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.emc.emf.InMemoryEmfModel;
import org.eclipse.epsilon.epl.EplModule;
import org.eclipse.epsilon.epl.execute.PatternMatch;
import org.eclipse.epsilon.epl.execute.model.PatternMatchModel;
import se.liu.ida.sas.pelab.text2vql.utilities.RailwayLoader;
import se.liu.ida.sas.pelab.text2vql.utilities.ResourcesHelper;
import se.liu.ida.sas.pelab.text2vql.utilities.evaluation.MatchSetEvaluator;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ValidateWithEOL extends MatchSetEvaluator<String,String>{
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

    @Override
    protected List<String> evaluate(String s, EObject model) {
        return null;
    }

    @Override
    protected boolean compare(List<String> truth, List<String> got) {
        return false;
    }

    @Override
    protected String parse(String query) {
        return null;
    }
}
