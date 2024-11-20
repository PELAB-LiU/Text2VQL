package se.liu.ida.sas.pelab.text2vql.refinery.templates;

import org.eclipse.emf.ecore.EPackage
import org.eclipse.emf.ecore.EClassifier
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EDataType
import org.eclipse.emf.ecore.EEnum
import org.eclipse.emf.ecore.ENamedElement
import java.nio.file.Files
import java.nio.file.Paths

//«»
class Ecore2Problem{
    def static String map2VQLProblem(EPackage epackage){
            return '''
                «Files.readString(Paths.get(Ecore2Problem.getClassLoader().getResource("templates/emf.problem").toURI()))»

                %%%%%%%%%%
                % Domain %
                %%%%%%%%%%
                «map(epackage)»
            '''
        }
    def static String map(EPackage epackage){
        return '''
            EPackage(«epackage.toName»).

            «FOR classifier : epackage.getEClassifiers»
            «mapClassifier(classifier)»
            «ENDFOR»
            «mapBuiltinDataTypes»
        '''
    }

    def static dispatch String mapClassifier(EClassifier eclassifier){
        throw new IllegalArgumentException("No type mapping is implemented for type "+eclassifier.name+".")
    }
    def static dispatch String mapClassifier(EClass eclass){
        return '''

            EClass(«eclass.toName»).
            EPackage::eClassifiers(«eclass.getEPackage.toName»,«eclass.toName»).
            EClassifier::ePackage(«eclass.toName»,«eclass.getEPackage.toName»).
                «FOR supertype : eclass.getESuperTypes»
                EClass::eSuperType(«eclass.toName», «supertype.toName»).
                «ENDFOR»

                «FOR reference : eclass.getEReferences»
                EReference(«reference.toName»).
                EClass::eReferences(«eclass.toName»,«reference.toName»).
                EReference::eReferenceType(«reference.toName»,«reference.getEReferenceType.toName»).
                «IF reference.getEOpposite !== null»
                    EReference::eOpposite(«reference.toName»,«reference.getEOpposite.toName»).
                «ENDIF»

                «ENDFOR»
                «FOR attribute : eclass.getEAttributes»
                EAttribute(«attribute.toName»).
                EClass::eAttributes(«eclass.toName»,«attribute.toName»).
                EAttribute::eAttributeType(«attribute.toName»,«attribute.getEAttributeType.toName»).
                «ENDFOR»
        '''
    }
    def static dispatch String mapClassifier(EDataType edatatype){
        return '''
            TODO implement edatatype
        '''
    }
    def static dispatch String mapClassifier(EEnum eenum){
        return '''
            EEnum(«eenum.toName»).
            EPackage::eClassifiers(«eenum.getEPackage.toName»,«eenum.toName»).
            EClassifier::ePackage(«eenum.toName»,«eenum.getEPackage.toName»).
            «FOR literal : eenum.getELiterals»
            EEnumLiteral(«literal.toName»).
            EEnum::literals(«eenum.toName»,«literal.toName»).
            «ENDFOR»
        '''
    }
    def static String mapBuiltinDataTypes(){
        return '''
            EPackage(EMFECore).
            «FOR datatype : TemplateUtil.builtinEMFDataTypes»

            EDataType(«datatype.toName»).
            EPackage::eClassifiers(EMFECore,«datatype.toName»).
            EClassifier::ePackage(«datatype.toName»,EMFECore).
            «ENDFOR»
        '''
    }
    def static toName(ENamedElement item){
        return TemplateUtil.toNameWithID(item)
    }
}