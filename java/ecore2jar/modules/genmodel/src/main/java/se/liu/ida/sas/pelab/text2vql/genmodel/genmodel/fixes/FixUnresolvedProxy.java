package se.liu.ida.sas.pelab.text2vql.genmodel.genmodel.fixes;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;

public class FixUnresolvedProxy {
    public EPackage fix(EPackage epackage) {
        epackage.getEClassifiers().forEach(cls -> {
            if(cls instanceof EClass clazz){
                Set<EClass> proxies = new HashSet<EClass>();
                clazz.getESuperTypes().forEach(st -> {
                    if(st.eIsProxy()){
                        proxies.add(st);
                    }
                });
                clazz.getEReferences().forEach(ref -> {
                    if(ref.getEType().eIsProxy()){
                        System.out.println("Rewrite reference type to EObject for: "+clazz.getName()+"."+ref.getName());
                        ref.setEType(EcorePackage.eINSTANCE.getEObject());
                    }
                });
                clazz.getEAttributes().forEach(att -> {
                    if(att.getEType().eIsProxy()){
                        System.out.println("Rewrite attribute type to EString for: "+clazz.getName()+"."+att.getName());
                        att.setEType(EcorePackage.eINSTANCE.getEString());
                    }
                });
                proxies.forEach(proxy -> {
                    System.out.println("Deleting proxy class: "+proxy.getName());
                    clazz.getESuperTypes().remove(proxy);
                });
            }
        });
    	return epackage;
    }
}
