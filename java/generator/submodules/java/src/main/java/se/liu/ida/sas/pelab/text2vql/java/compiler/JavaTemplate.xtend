package se.liu.ida.sas.pelab.text2vql.java.compiler

//«»
class JavaTemplate{
    def generateJavaCode(String classcode, String... packages){
        return '''
import java.util.*;
import org.eclipse.emf.common.util.*;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
«FOR pkg : packages»
import «pkg».*;
«ENDFOR»


«classcode» 
'''
    }
}