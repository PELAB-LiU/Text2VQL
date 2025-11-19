package se.liu.ida.sas.pelab.text2vql.genmodel.genmodel.fixes;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;

public class FixEnumLiteralName {
    public EPackage fix(EPackage epackage) {
		epackage.getEClassifiers().stream()
			.filter(cls -> (cls instanceof EEnum)).map(cls -> (EEnum) cls)
        	.forEach(eenum -> {
				eenum.getELiterals().forEach(literal -> {
					if(!Character.isLetter(literal.getName().charAt(0))){
						System.out.println("fixing enum in "+epackage.eResource().getURI()+"#"+eenum.getName()+"."+literal.getName());
						literal.setName("_"+literal.getName());
					}
				});
			});
    	return epackage;
    }
}
