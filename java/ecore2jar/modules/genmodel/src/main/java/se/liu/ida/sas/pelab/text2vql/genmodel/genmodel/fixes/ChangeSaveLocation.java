package se.liu.ida.sas.pelab.text2vql.genmodel.genmodel.fixes;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;

public class ChangeSaveLocation {
    private String target;
    private List<Resource> mapped = new ArrayList<Resource>();
    
    public ChangeSaveLocation(String target){
        this.target = target.replace("model.genmodel","model-fixed.ecore");
    }
    
    public EPackage fix(EPackage epackage) {
  		if(!mapped.contains(epackage.eResource())) {
   			mapped.add(epackage.eResource());
   			epackage.eResource().setURI(URI.createFileURI(target));
   		}
    	return epackage;
    }
}
