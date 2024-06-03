package se.liu.ida.sas.pelab.profile

import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareFeature
import org.eclipse.viatra.query.patternlanguage.emf.vql.Constraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.CheckConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCompositionConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.TypeCheckConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.EClassifierConstraint
import org.eclipse.viatra.query.patternlanguage.emf.vql.EnumValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.JavaConstantValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference
import org.eclipse.viatra.query.patternlanguage.emf.vql.FunctionEvaluationValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.AggregatedValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.NumberValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.BoolValue
import org.eclipse.viatra.query.patternlanguage.emf.vql.ListValue
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody
import org.eclipse.viatra.query.patternlanguage.emf.vql.LocalVariable
import org.eclipse.viatra.query.patternlanguage.emf.vql.Parameter
import org.eclipse.viatra.query.patternlanguage.emf.vql.ParameterRef
import javax.tools.JavaCompiler.CompilationTask
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCall

class QueryProfile {
	
	/**
	 * Process Queries
	 */
	var int main = 0;
	var int patterns = 0;
	var int or = 0;
	def int aux(){patterns-main}
	def void processParsed(PatternParsingResults parsed){
		main++
		parsed.patterns.forEach[it.processPattern]
	}
	def void processPattern(Pattern pattern){
		patterns++
		or-- //as every pattern contains at least one body
		pattern.bodies.forEach[it.processBody]
		pattern.parameters.forEach[it.processVariable]
	}
	def void processBody(PatternBody body){
		or++
		body.constraints.forEach[it.processConstraint]
		body.variables.forEach[it.processVariable]
	}
	private def printPattern()'''
	#patterns=«patterns»
		#main=«main»
		#auxiliary=«aux»
	#or=«or»
	'''
	/**
	 * Process Constraints
	 */
	def int constraints(){compare+check+composition+type+path+classifier}
	def int compare(){compare_eq+compare_neq}
	def int composition() {patterncomposition_neg+patterncomposition_pos}
	def int patternneg() {patterncomposition_neg-pathcall-classifiercall-typecall}
	def int patternpos(){patterncomposition_pos}
	var int compare_eq = 0;
	var int compare_neq = 0;
	var int check = 0;
	var int patterncomposition_pos = 0;
	var int patterncomposition_neg = 0;
	var int path = 0;
	var int type = 0;
	var int classifier = 0;
	def dispatch void processConstraint(CompareConstraint constraint){
		switch(constraint.feature) {
			case CompareFeature.EQUALITY:
				compare_eq++
			case CompareFeature.INEQUALITY:
				compare_neq++
		}
		constraint.leftOperand.processValue
		constraint.rightOperand.processValue
	}
	def dispatch void processConstraint(CheckConstraint constraint){
		check++
	}
	
	def dispatch void processConstraint(PatternCompositionConstraint constraint){
		if(constraint.negative)
			patterncomposition_neg++
		else
			patterncomposition_pos++
		 constraint.call.processCompositionCall
	}
	def dispatch void processConstraint(PathExpressionConstraint constraint){
		path++
		constraint.sourceType // TODO
		constraint.edgeTypes// TODO count?
		
		constraint.transitive
		
		constraint.src.processValue
		constraint.dst.processValue
	}
	
	def dispatch void processConstraint(TypeCheckConstraint constraint){
		type++
		constraint.transitive
		constraint.type // TODO ?
		constraint.^var.processValue
	}
	def dispatch void processConstraint(EClassifierConstraint constraint){
		classifier++
		constraint.transitive
		constraint.type //TODO ?
		constraint.^var.processValue
	}
	private def printConstraints()'''
	#contraints=«constraints»
		#comapre=«compare»
			#equal=«compare_eq»
			#notequal=«compare_neq»
		#check=«check»
		#composition=«composition»
			#positive=«patterncomposition_pos»
				 #patterncall=«patternpos»
			#negative=«patterncomposition_neg»
				#pathcall=«pathcall»
				#typecall=«typecall»
				#classifiercall=«classifiercall»
				#pattern=«patternneg»
		#type=«type»
		#path=«path»
		#classifier=«classifier»
	'''
	/**
	 * Process .call
	 */
	var int pathcall = 0;
	var int patterncall = 0;
	var int typecall = 0;
	var int classifiercall = 0;
	def dispatch void processCompositionCall(PathExpressionConstraint call){
		pathcall++
		path-- //To correct for double counting as call is indirect constraint
		call.processConstraint
	}
	def dispatch void processCompositionCall(PatternCall call){
		patterncall++
		//To correct for double counting as call is indirect constraint
		call.parameters.forEach[it.processValue]
	}
	def dispatch void processCompositionCall(TypeCheckConstraint call){
		typecall++
		type-- //To correct for double counting as call is indirect constraint
		call.processConstraint
	}
	def dispatch void processCompositionCall(EClassifierConstraint call){
		classifiercall++
		classifier-- //To correct for double counting as call is indirect constraint
		call.processConstraint
	}
	
	/**
	 * Process Values
	 */
	var int enumvalue = 0;
	var int javavalue = 0;
	var int variable = 0;
	var int variable_agg = 0;
	var int eval = 0;
	var int aggregate = 0;
	var int stringvalue = 0;
	var int numvalue = 0;
	var int boolvalue = 0;
	var int listvalue = 0;
	def dispatch void processValue(EnumValue value){
		enumvalue++
	}
	def dispatch void processValue(JavaConstantValue value){
		javavalue++
	}
	def dispatch void processValue(VariableReference value){
		if(value.aggregator)
			variable_agg++
		else
			variable++
		value.variable
	}
	def dispatch void processValue(FunctionEvaluationValue value){
		eval++
	}
	val aggmap = <String,Integer>newHashMap
	def dispatch void processValue(AggregatedValue value){
		aggregate++
		val name = value.aggregator.simpleName
		aggmap.putIfAbsent(name, 0)
		aggmap.put(name, aggmap.get(name)+1)
		value.call.processCallAgg
	}
	def dispatch void processValue(StringValue value){
		stringvalue++
	}
	def dispatch void processValue(NumberValue value){
		numvalue++
	}
	def dispatch void processValue(BoolValue value){
		boolvalue++
	}
	def dispatch void processValue(ListValue value){
		listvalue++
	}
	private def printValues()'''
	#values=«enumvalue+javavalue+variable+variable_agg+eval+aggregate+stringvalue+numvalue+boolvalue+listvalue»
		#enum=«enumvalue»
		#javaconstant=«javavalue»
		#variableref=«variable+variable_agg»
			#variableref=«variable»
			#aggregatorref=«variable_agg»
		#function=«eval»
		#aggregated=«aggregate»
			#pattern=«patterncallagg»
			#classifier=«classifiercallagg»
			#path=«pathcallagg»
			#type=«classifiercallagg»
		#literals=«stringvalue+numvalue+boolvalue+listvalue»
			#string=«stringvalue»
			#number=«numvalue»
			#bool=«boolvalue»
			#list=«listvalue»
	'''
	
	var int pathcallagg = 0;
	var int patterncallagg = 0;
	var int typecallagg = 0;
	var int classifiercallagg = 0;
	def dispatch void processCallAgg(PathExpressionConstraint call){
		pathcallagg++
		path-- //To correct for double counting as call is indirect constraint
		call.processConstraint
	}
	def dispatch void processCallAgg(PatternCall call){
		patterncallagg++
		//To correct for double counting as call is indirect constraint
		call.parameters.forEach[it.processValue]
	}
	def dispatch void processCallAgg(TypeCheckConstraint call){
		typecallagg++
		type-- //To correct for double counting as call is indirect constraint
		call.processConstraint
	}
	def dispatch void processCallAgg(EClassifierConstraint call){
		classifiercallagg++
		classifier-- //To correct for double counting as call is indirect constraint
		call.processConstraint
	}
	
	
	/**
	 * Provess Variable
	 */
	 var int normalvar = 0;
	 var int local = 0;
	 var int parameter = 0;
	 var int paramref = 0;
	 def dispatch void processVariable(Variable varialbe){
	 	normalvar++
	 }
	 def dispatch void processVariable(LocalVariable variable){
	 	local++
	 }
	 def dispatch void processVariable(Parameter variable){
	 	parameter++
	 }
	 def dispatch void processVariable(ParameterRef variable){
	 	paramref++
	 }
	 private def printVariables()'''
	 #variables=«local+parameter+paramref+normalvar»
	 	#normalvar=«normalvar»
	 	#local=«local»
	 	#parameter=«parameter»
	 	#paramref=«paramref»
	 '''
	 /**
	  * Annotations are not covered
	  * Imports are not covered
	  */
	  
	override toString() {
		'''
		«printPattern»
		«printConstraints»
		«printValues»
		«printVariables»
		'''
	}
	static def header()'''patterns,lines,auxiliary,or,constraint,classifier,path,composition,find,neg_find,comapre,eq,neq,check,string,number,bool,enum,aggregate'''
	def data()'''«patterns»,«patterns+or+constraints»,«aux»,«or»,«constraints»,«classifier»,«path»,«composition»,«patterncomposition_pos»,«patterncomposition_neg»,«compare»,«compare_eq»,«compare_neq»,«check»,«stringvalue»,«numvalue»,«boolvalue»,«enumvalue»,«aggregate»'''
	def aggs()'''
	«FOR entry : aggmap.entrySet»
		#«entry.key»=«entry.value»
	«ENDFOR»
	'''
}











