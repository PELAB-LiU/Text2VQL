package tools.refinery.generator.cli;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;
import tools.refinery.language.model.problem.*;

import java.util.HashMap;
import java.util.Map;

public class AssertionStream2EMF {
	private final Map<AssertionArgument, EObject> objects = new HashMap();
	private final Object root = null;
	private final EFactory factory = null;
	private final EPackage epackage = null;
	private void makeInstantiate(Assertion assertion){
		var args = assertion.getArguments();
		if(args.size()!=1)
			return;

		var value = (LogicConstant) assertion.getValue();
		if(value.getLogicValue() != LogicValue.TRUE)
			return;

		var classifier = epackage.getEClassifier(assertion.getRelation().getName());
		if(classifier==null)
			return;

		if(classifier instanceof EClass eclass){
			EObject object = factory.create(eclass);
			objects.put(args.get(0), object);
		}
		if(classifier instanceof EEnum eenum){

		}
	}

	private void makeRelation(Assertion assertion){
		var args = assertion.getArguments();
		if(args.size()!=2)
			return;

		var value = (LogicConstant) assertion.getValue();
		if(value.getLogicValue() != LogicValue.TRUE)
			return;

		var name = assertion.getRelation().getName();
		var host = objects.get(args.get(0));

		/*if(feature==null)
			return;

		var target = objects.get(args.get(1));

		if(feature.isMany()){
			var list = (EList) host.eGet(feature);
			list.add(target);
		} else {
			host.eSet(feature, target);
		}*/
	}
}
