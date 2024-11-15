package se.liu.ida.sas.pelab.text2vql.templates;

import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClassType
import org.eclipse.viatra.query.patternlanguage.emf.vql.Type
import org.eclipse.viatra.query.patternlanguage.emf.vql.Constraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.ReferenceType
import org.eclipse.viatra.query.patternlanguage.emf.vql.Parameter
import org.eclipse.viatra.query.patternlanguage.emf.vql.Expression
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCompositionConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCall
import org.eclipse.viatra.query.patternlanguage.emf.vql.CallableRelation
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.NumberValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.BoolValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.EnumValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareFeature
import org.eclipse.viatra.query.patternlanguage.emf.vql.EClassifierConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.AggregatedValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.CheckConstraint


import org.eclipse.emf.ecore.ENamedElement


//«»
class VQLTemplate {
    def static header(){
        return '''
            pattern foo()
        '''
    }
    def static query(Pattern pattern){
        return '''
        pattern «pattern.name»(«FOR param : pattern.parameters SEPARATOR ', '»«growExpression(param)»«ENDFOR»)
        «FOR body : pattern.bodies SEPARATOR ' or '»
        {
            «FOR constraint : body.constraints»
            «growConstraint(constraint)»
            «ENDFOR»
        }
        «ENDFOR»
        '''
    }
    def static dispatch growConstraint(Constraint constraint){
        return '''Constraint not implemented: «constraint.class.name»'''
    }
    def static dispatch growConstraint(EClassifierConstraint constraint){
        return '''«constraint.type.growType»(«constraint.^var.growExpression»);'''
    }
    def static dispatch growConstraint(PathExpressionConstraint constraint){
        return '''«constraint.sourceType.growType».«FOR edge : constraint.edgeTypes»«edge.growType»«ENDFOR»(«constraint.src.growExpression»,«constraint.dst.growExpression»);'''
    }
    def static dispatch growConstraint(PatternCompositionConstraint constraint){
        return '''«IF constraint.negative»neg «ENDIF»«constraint.call.growCall»;'''
    }
    def static dispatch growConstraint(CompareConstraint constraint){
        return '''«constraint.leftOperand.growExpression»«IF constraint.feature === CompareFeature.EQUALITY» == «ELSE» != «ENDIF»«constraint.rightOperand.growExpression»;'''
    }
    def static dispatch growConstraint(CheckConstraint constraint){
        return '''check(«constraint.expression»);'''
    }

    def static dispatch String growCall(CallableRelation call){
        return '''Callable relation not implemented: «call»'''
    }
    def static dispatch String growCall(PatternCall call){
        return '''find «call.patternRef.name»(«FOR param: call.parameters SEPARATOR ','»«param.growExpression»«ENDFOR»)'''
    }
    def static dispatch String growCall(Constraint call){
        return '''«growConstraint(call)»'''
    }




    def static dispatch growExpression(Expression expression){
        return '''Expression not implemented: «expression»'''
    }
    def static dispatch growExpression(Parameter param){
        return '''«param.name»: «growType(param.type)»'''
    }
    def static dispatch growExpression(VariableReference param){
        if(param.isAggregator){
            return '''#'''
        } else {
            return '''«param.variable.name»'''
        }
    }
    def static dispatch growExpression(AggregatedValue param){
        return '''«param.aggregator.simpleName» «param.call.growCall»'''
    }
    def static dispatch growExpression(StringValue param){
        return '''"«param.value»"'''
    }
    def static dispatch growExpression(NumberValue param){
        return '''«IF param.negative»-«ENDIF»«param.value.value»'''
    }
    def static dispatch growExpression(BoolValue param){
        return '''«param.value.isIsTrue»'''
    }
    def static dispatch growExpression(EnumValue param){
        return '''«param.enumeration.toName»::«param.literal.toName»'''
    }

    def static dispatch growType(Type type){
        return '''Type not implemented: «type»'''
    }
    def static dispatch growType(ClassType type){
        return '''«type.getClassname.toName»'''
    }
    def static dispatch growType(ReferenceType type){
        return '''«type.getRefname.toName»'''
    }

    def static toName(ENamedElement item){
        return TemplateUtil.toName(item)
    }
}