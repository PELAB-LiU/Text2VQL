package se.liu.ida.sas.pelab.text2vql.genmodel.genmodel.fixes;

import org.eclipse.emf.ecore.EPackage;

public class FixMissingNSURI {
    public EPackage fix(EPackage epackage) {
		var original = epackage.getName();
		var lower = epackage.getName().toLowerCase();

		if(!lower.equals(original)){
			System.out.println("Package name is not lower case. "+original+"-->"+lower);
			epackage.setName(lower);
		}		

		if(epackage.getNsURI()==null) {
			System.out.println("Missing package name for package "+epackage.eResource().getURI()+"#"+ original);
    		epackage.setNsURI(lower);
    	}
		
    	return epackage;
    }
}
