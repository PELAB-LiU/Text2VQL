package se.liu.ida.sas.pelab.text2vql.vql;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.runtime.api.AdvancedViatraQueryEngine;
import org.eclipse.viatra.query.runtime.api.IQuerySpecification;
import org.eclipse.viatra.query.runtime.api.ViatraQueryEngine;
import org.eclipse.viatra.query.runtime.emf.EMFScope;

public class StatelessVQLEvaluation {
    public List<String> getMatches(Resource instances, PatternParsingResults queries, String query){
        //Make it managed query engine
        AdvancedViatraQueryEngine engine = (AdvancedViatraQueryEngine) ViatraQueryEngine.on(new EMFScope(instances));
        List<String> matches = new LinkedList<>();
        Optional<? extends IQuerySpecification<?>> op = queries.getQuerySpecification(query);
        if(op.isPresent()){
            IQuerySpecification<?> specification = op.get();
            var matcher = engine.getMatcher(specification);
            matcher.forEachMatch(match -> matches.add(match.prettyPrint()));
            return matches;
        } else {
            return null;
        }
        
    }
}
