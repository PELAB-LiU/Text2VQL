package se.liu.ida.sas.pelab.text2vql.genmodel.genmodel.fixes;

import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EPackage;

public class FixEDataTypeMissingInstanceClassName {
    public EPackage fix(EPackage epackage){
        epackage.getEClassifiers().stream()
        		.filter(cls -> (cls instanceof EDataType) && !(cls instanceof EEnum)).map(cls -> (EDataType) cls)
        		.filter(data -> data.getInstanceClassName()==null)
        		.forEach(cls -> cls.setInstanceClassName("Object"));
        return epackage;
    }
}
