package se.liu.ida.sas.pelab.text2vql.utilities.modeling;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.*;

public class PackageHelper {
    public final EPackage epackage;
    public final EFactory factory;

    public PackageHelper(EPackage epackage){
        this.epackage = epackage;
        factory = epackage.getEFactoryInstance();
    }

    public EObject make(String eClass){
        EClass cls = (EClass) epackage.getEClassifier(eClass);
        return factory.create(cls);
    }
    public <T> void link(EObject object, String feature, T value){
        EStructuralFeature relation = object.eClass().getEStructuralFeature(feature);
        if(object.eGet(relation) instanceof EList list){
            list.add(value);
        } else {
            object.eSet(relation, value);
        }
    }
    public <T> void set(EObject object, String feature, T value){
        EStructuralFeature relation = object.eClass().getEStructuralFeature(feature);
        object.eSet(relation, value);
    }

    public <T> void add(EObject object, String feature, T value){
        EStructuralFeature relation = object.eClass().getEStructuralFeature(feature);
        ((EList) object.eGet(relation)).add(value);
    }

}
