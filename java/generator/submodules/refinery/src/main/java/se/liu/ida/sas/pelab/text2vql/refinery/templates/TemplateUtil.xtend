package se.liu.ida.sas.pelab.text2vql.refinery.templates;

import org.eclipse.emf.ecore.ENamedElement
import org.eclipse.emf.ecore.EcorePackage
import org.eclipse.emf.ecore.EDataType

class TemplateUtil{
    def static toNameWithID(ENamedElement enamedelement){
        if(enamedelement instanceof EDataType && builtinEMFDataTypes.contains(enamedelement)){
            return enamedelement.name
        }
        return enamedelement.name+"_"+enamedelement.hashCode()
    }
    def static toName(ENamedElement enamedelement){
        return enamedelement.name
    }
    public static val builtinEMFDataTypes = #{
        //EcorePackage.Literals.EBIG_DECIMAL,
        //EcorePackage.Literals.EBIG_INTEGER,
        EcorePackage.Literals.EBOOLEAN,
        //EcorePackage.Literals.EBOOLEAN_OBJECT,
        EcorePackage.Literals.EBYTE,
        //EcorePackage.Literals.EBYTE_ARRAY,
        //EcorePackage.Literals.EBYTE_OBJECT,
        EcorePackage.Literals.ECHAR,
        //EcorePackage.Literals.ECHARACTER_OBJECT,
        //EcorePackage.Literals.EDATE,
        //EcorePackage.Literals.EDIAGNOSTIC_CHAIN,
        EcorePackage.Literals.EDOUBLE,
        //EcorePackage.Literals.EDOUBLE_OBJECT,
        //EcorePackage.Literals.EE_LIST,
        //EcorePackage.Literals.EENUMERATOR,
        //EcorePackage.Literals.EFEATURE_MAP,
        //EcorePackage.Literals.EFEATURE_MAP_ENTRY,
        EcorePackage.Literals.EFLOAT,
        //EcorePackage.Literals.EFLOAT_OBJECT,
        EcorePackage.Literals.EINT,
        //EcorePackage.Literals.EINTEGER_OBJECT,
        //EcorePackage.Literals.EJAVA_CLASS,
        EcorePackage.Literals.ELONG,
        //EcorePackage.Literals.ELONG_OBJECT,
        //EcorePackage.Literals.EMAP,
        //EcorePackage.Literals.ERESOURCE,
        //EcorePackage.Literals.ERESOURCE_SET,
        EcorePackage.Literals.ESHORT,
        //EcorePackage.Literals.ESHORT_OBJECT,
        EcorePackage.Literals.ESTRING//,
        //EcorePackage.Literals.ETREE_ITERATOR
    }
}