package se.liu.ida.sas.pelab.profile;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.patternlanguage.emf.vql.AggregatedValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.BoolValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CallableRelation;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CheckConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ClosureType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.CompareFeature;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Constraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.EClassifierConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.EnumValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ExecutionType;
import org.eclipse.viatra.query.patternlanguage.emf.vql.FunctionEvaluationValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.JavaConstantValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ListValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.LocalVariable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.NumberValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Parameter;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ParameterRef;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PathExpressionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Pattern;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternBody;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCall;
import org.eclipse.viatra.query.patternlanguage.emf.vql.PatternCompositionConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.StringValue;
import org.eclipse.viatra.query.patternlanguage.emf.vql.TypeCheckConstraint;
import org.eclipse.viatra.query.patternlanguage.emf.vql.ValueReference;
import org.eclipse.viatra.query.patternlanguage.emf.vql.Variable;
import org.eclipse.viatra.query.patternlanguage.emf.vql.VariableReference;
import org.eclipse.xtend2.lib.StringConcatenation;
import org.eclipse.xtext.xbase.lib.CollectionLiterals;

@SuppressWarnings("all")
public class QueryProfile {
  /**
   * Process Queries
   */
  private int main = 0;

  private int patterns = 0;

  private int or = 0;

  private int mods = 0;

  public int aux() {
    return (this.patterns - this.main);
  }

  public void processParsed(final PatternParsingResults parsed) {
    this.main++;
    final Consumer<Pattern> _function = (Pattern it) -> {
      this.processPattern(it);
    };
    parsed.getPatterns().forEach(_function);
  }

  public void processPattern(final Pattern pattern) {
    this.patterns++;
    this.or--;
    if (((Boolean.valueOf(pattern.getModifiers().isPrivate()) != Boolean.valueOf(false)) || (pattern.getModifiers().getExecution() != ExecutionType.UNSPECIFIED))) {
      this.mods++;
    }
    final Consumer<PatternBody> _function = (PatternBody it) -> {
      this.processBody(it);
    };
    pattern.getBodies().forEach(_function);
    final Consumer<Variable> _function_1 = (Variable it) -> {
      this.processVariable(it);
    };
    pattern.getParameters().forEach(_function_1);
  }

  public void processBody(final PatternBody body) {
    this.or++;
    final Consumer<Constraint> _function = (Constraint it) -> {
      this.processConstraint(it);
    };
    body.getConstraints().forEach(_function);
    final Consumer<Variable> _function_1 = (Variable it) -> {
      this.processVariable(it);
    };
    body.getVariables().forEach(_function_1);
  }

  private CharSequence printPattern() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("#patterns=");
    _builder.append(this.patterns);
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#main=");
    _builder.append(this.main, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#auxiliary=");
    int _aux = this.aux();
    _builder.append(_aux, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#non-deault modifiers=");
    _builder.append(this.mods, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("#or=");
    _builder.append(this.or);
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  /**
   * Process Constraints
   */
  public int constraints() {
    int _compare = this.compare();
    int _plus = (_compare + this.check);
    int _composition = this.composition();
    int _plus_1 = (_plus + _composition);
    int _plus_2 = (_plus_1 + this.type);
    int _plus_3 = (_plus_2 + this.path);
    return (_plus_3 + this.classifier);
  }

  public int compare() {
    return (this.compare_eq + this.compare_neq);
  }

  public int composition() {
    return (this.patterncomposition_neg + this.patterncomposition_pos);
  }

  public int patternneg() {
    return (((this.patterncomposition_neg - this.pathcall) - this.classifiercall) - this.typecall);
  }

  public int patternpos() {
    return this.patterncomposition_pos;
  }

  private int compare_eq = 0;

  private int compare_neq = 0;

  private int check = 0;

  private int patterncomposition_pos = 0;

  private int patterncomposition_neg = 0;

  private int path = 0;

  private int type = 0;

  private int classifier = 0;

  protected void _processConstraint(final CompareConstraint constraint) {
    CompareFeature _feature = constraint.getFeature();
    if (_feature != null) {
      switch (_feature) {
        case EQUALITY:
          this.compare_eq++;
          break;
        case INEQUALITY:
          this.compare_neq++;
          break;
        default:
          break;
      }
    }
    this.processValue(constraint.getLeftOperand());
    this.processValue(constraint.getRightOperand());
  }

  protected void _processConstraint(final CheckConstraint constraint) {
    this.check++;
  }

  protected void _processConstraint(final PatternCompositionConstraint constraint) {
    boolean _isNegative = constraint.isNegative();
    if (_isNegative) {
      this.patterncomposition_neg++;
    } else {
      this.patterncomposition_pos++;
    }
    this.processCompositionCall(constraint.getCall());
  }

  protected void _processConstraint(final PathExpressionConstraint constraint) {
    this.path++;
    constraint.getSourceType();
    constraint.getEdgeTypes();
    constraint.getTransitive();
    this.processValue(constraint.getSrc());
    this.processValue(constraint.getDst());
  }

  protected void _processConstraint(final TypeCheckConstraint constraint) {
    this.type++;
    constraint.getTransitive();
    constraint.getType();
    this.processValue(constraint.getVar());
  }

  protected void _processConstraint(final EClassifierConstraint constraint) {
    this.classifier++;
    constraint.getTransitive();
    constraint.getType();
    this.processValue(constraint.getVar());
  }

  private CharSequence printConstraints() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("#contraints=");
    int _constraints = this.constraints();
    _builder.append(_constraints);
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#comapre=");
    int _compare = this.compare();
    _builder.append(_compare, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#equal=");
    _builder.append(this.compare_eq, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#notequal=");
    _builder.append(this.compare_neq, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#check=");
    _builder.append(this.check, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#composition=");
    int _composition = this.composition();
    _builder.append(_composition, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("closures");
    _builder.newLine();
    _builder.append("\t\t\t");
    _builder.append("#original=");
    int _composition_1 = this.composition();
    int _minus = (_composition_1 - this.tra);
    int _minus_1 = (_minus - this.reftra);
    _builder.append(_minus_1, "\t\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t");
    _builder.append("#transitive=");
    _builder.append(this.tra, "\t\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t");
    _builder.append("#refleive transitive=");
    _builder.append(this.reftra, "\t\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#positive=");
    _builder.append(this.patterncomposition_pos, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t ");
    _builder.append("#patterncall=");
    int _patternpos = this.patternpos();
    _builder.append(_patternpos, "\t\t\t ");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#negative=");
    _builder.append(this.patterncomposition_neg, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t");
    _builder.append("#pathcall=");
    _builder.append(this.pathcall, "\t\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t");
    _builder.append("#typecall=");
    _builder.append(this.typecall, "\t\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t");
    _builder.append("#classifiercall=");
    _builder.append(this.classifiercall, "\t\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t\t");
    _builder.append("#pattern=");
    int _patternneg = this.patternneg();
    _builder.append(_patternneg, "\t\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#type=");
    _builder.append(this.type, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#path=");
    _builder.append(this.path, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#classifier=");
    _builder.append(this.classifier, "\t");
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  /**
   * Process .call
   */
  private int pathcall = 0;

  private int patterncall = 0;

  private int typecall = 0;

  private int classifiercall = 0;

  private int reftra = 0;

  private int tra = 0;

  protected void _processCompositionCall(final PathExpressionConstraint call) {
    this.pathcall++;
    this.path--;
    this.processConstraint(call);
    this.processClosure(call.getTransitive());
  }

  protected void _processCompositionCall(final PatternCall call) {
    this.patterncall++;
    final Consumer<ValueReference> _function = (ValueReference it) -> {
      this.processValue(it);
    };
    call.getParameters().forEach(_function);
    this.processClosure(call.getTransitive());
  }

  protected void _processCompositionCall(final TypeCheckConstraint call) {
    this.typecall++;
    this.type--;
    this.processConstraint(call);
    this.processClosure(call.getTransitive());
  }

  protected void _processCompositionCall(final EClassifierConstraint call) {
    this.classifiercall++;
    this.classifier--;
    this.processConstraint(call);
    this.processClosure(call.getTransitive());
  }

  public int processClosure(final ClosureType closure) {
    int _xifexpression = (int) 0;
    if ((closure == ClosureType.REFLEXIVE_TRANSITIVE)) {
      _xifexpression = this.reftra++;
    } else {
      int _xifexpression_1 = (int) 0;
      if ((closure == ClosureType.TRANSITIVE)) {
        _xifexpression_1 = this.tra++;
      }
      _xifexpression = _xifexpression_1;
    }
    return _xifexpression;
  }

  /**
   * Process Values
   */
  private int enumvalue = 0;

  private int javavalue = 0;

  private int variable = 0;

  private int variable_agg = 0;

  private int eval = 0;

  private int aggregate = 0;

  private int stringvalue = 0;

  private int numvalue = 0;

  private int boolvalue = 0;

  private int listvalue = 0;

  protected void _processValue(final EnumValue value) {
    this.enumvalue++;
  }

  protected void _processValue(final JavaConstantValue value) {
    this.javavalue++;
  }

  protected void _processValue(final VariableReference value) {
    boolean _isAggregator = value.isAggregator();
    if (_isAggregator) {
      this.variable_agg++;
    } else {
      this.variable++;
    }
    value.getVariable();
  }

  protected void _processValue(final FunctionEvaluationValue value) {
    this.eval++;
  }

  private final HashMap<String, Integer> aggmap = CollectionLiterals.<String, Integer>newHashMap();

  protected void _processValue(final AggregatedValue value) {
    this.aggregate++;
    final String name = value.getAggregator().getSimpleName();
    this.aggmap.putIfAbsent(name, Integer.valueOf(0));
    Integer _get = this.aggmap.get(name);
    int _plus = ((_get).intValue() + 1);
    this.aggmap.put(name, Integer.valueOf(_plus));
    this.processCallAgg(value.getCall());
  }

  protected void _processValue(final StringValue value) {
    this.stringvalue++;
  }

  protected void _processValue(final NumberValue value) {
    this.numvalue++;
  }

  protected void _processValue(final BoolValue value) {
    this.boolvalue++;
  }

  protected void _processValue(final ListValue value) {
    this.listvalue++;
  }

  private CharSequence printValues() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("#values=");
    _builder.append((((((((((this.enumvalue + this.javavalue) + this.variable) + this.variable_agg) + this.eval) + this.aggregate) + this.stringvalue) + this.numvalue) + this.boolvalue) + this.listvalue));
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#enum=");
    _builder.append(this.enumvalue, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#javaconstant=");
    _builder.append(this.javavalue, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#variableref=");
    _builder.append((this.variable + this.variable_agg), "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#variableref=");
    _builder.append(this.variable, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#aggregatorref=");
    _builder.append(this.variable_agg, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#function=");
    _builder.append(this.eval, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#aggregated=");
    _builder.append(this.aggregate, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#pattern=");
    _builder.append(this.patterncallagg, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#classifier=");
    _builder.append(this.classifiercallagg, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#path=");
    _builder.append(this.pathcallagg, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#type=");
    _builder.append(this.classifiercallagg, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#literals=");
    _builder.append((((this.stringvalue + this.numvalue) + this.boolvalue) + this.listvalue), "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#string=");
    _builder.append(this.stringvalue, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#number=");
    _builder.append(this.numvalue, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#bool=");
    _builder.append(this.boolvalue, "\t\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t\t");
    _builder.append("#list=");
    _builder.append(this.listvalue, "\t\t");
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  private int pathcallagg = 0;

  private int patterncallagg = 0;

  private int typecallagg = 0;

  private int classifiercallagg = 0;

  protected void _processCallAgg(final PathExpressionConstraint call) {
    this.pathcallagg++;
    this.path--;
    this.processConstraint(call);
  }

  protected void _processCallAgg(final PatternCall call) {
    this.patterncallagg++;
    final Consumer<ValueReference> _function = (ValueReference it) -> {
      this.processValue(it);
    };
    call.getParameters().forEach(_function);
  }

  protected void _processCallAgg(final TypeCheckConstraint call) {
    this.typecallagg++;
    this.type--;
    this.processConstraint(call);
  }

  protected void _processCallAgg(final EClassifierConstraint call) {
    this.classifiercallagg++;
    this.classifier--;
    this.processConstraint(call);
  }

  /**
   * Provess Variable
   */
  private int normalvar = 0;

  private int local = 0;

  private int parameter = 0;

  private int paramref = 0;

  protected void _processVariable(final Variable varialbe) {
    this.normalvar++;
  }

  protected void _processVariable(final LocalVariable variable) {
    this.local++;
  }

  protected void _processVariable(final Parameter variable) {
    this.parameter++;
  }

  protected void _processVariable(final ParameterRef variable) {
    this.paramref++;
  }

  private CharSequence printVariables() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("#variables=");
    _builder.append((((this.local + this.parameter) + this.paramref) + this.normalvar));
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#normalvar=");
    _builder.append(this.normalvar, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#local=");
    _builder.append(this.local, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#parameter=");
    _builder.append(this.parameter, "\t");
    _builder.newLineIfNotEmpty();
    _builder.append("\t");
    _builder.append("#paramref=");
    _builder.append(this.paramref, "\t");
    _builder.newLineIfNotEmpty();
    return _builder;
  }

  /**
   * Annotations are not covered
   * Imports are not covered
   */
  @Override
  public String toString() {
    StringConcatenation _builder = new StringConcatenation();
    CharSequence _printPattern = this.printPattern();
    _builder.append(_printPattern);
    _builder.newLineIfNotEmpty();
    CharSequence _printConstraints = this.printConstraints();
    _builder.append(_printConstraints);
    _builder.newLineIfNotEmpty();
    CharSequence _printValues = this.printValues();
    _builder.append(_printValues);
    _builder.newLineIfNotEmpty();
    CharSequence _printVariables = this.printVariables();
    _builder.append(_printVariables);
    _builder.newLineIfNotEmpty();
    return _builder.toString();
  }

  public static CharSequence header() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append("patterns,lines,auxiliary,or,constraint,classifier,path,composition,find,neg_find,comapre,eq,neq,check,string,number,bool,enum,aggregate");
    return _builder;
  }

  public CharSequence data() {
    StringConcatenation _builder = new StringConcatenation();
    _builder.append(this.patterns);
    _builder.append(",");
    int _constraints = this.constraints();
    int _plus = ((this.patterns + this.or) + _constraints);
    _builder.append(_plus);
    _builder.append(",");
    int _aux = this.aux();
    _builder.append(_aux);
    _builder.append(",");
    _builder.append(this.or);
    _builder.append(",");
    int _constraints_1 = this.constraints();
    _builder.append(_constraints_1);
    _builder.append(",");
    _builder.append(this.classifier);
    _builder.append(",");
    _builder.append(this.path);
    _builder.append(",");
    int _composition = this.composition();
    _builder.append(_composition);
    _builder.append(",");
    _builder.append(this.patterncomposition_pos);
    _builder.append(",");
    _builder.append(this.patterncomposition_neg);
    _builder.append(",");
    int _compare = this.compare();
    _builder.append(_compare);
    _builder.append(",");
    _builder.append(this.compare_eq);
    _builder.append(",");
    _builder.append(this.compare_neq);
    _builder.append(",");
    _builder.append(this.check);
    _builder.append(",");
    _builder.append(this.stringvalue);
    _builder.append(",");
    _builder.append(this.numvalue);
    _builder.append(",");
    _builder.append(this.boolvalue);
    _builder.append(",");
    _builder.append(this.enumvalue);
    _builder.append(",");
    _builder.append(this.aggregate);
    return _builder;
  }

  public CharSequence aggs() {
    StringConcatenation _builder = new StringConcatenation();
    {
      Set<Map.Entry<String, Integer>> _entrySet = this.aggmap.entrySet();
      for(final Map.Entry<String, Integer> entry : _entrySet) {
        _builder.append("#");
        String _key = entry.getKey();
        _builder.append(_key);
        _builder.append("=");
        Integer _value = entry.getValue();
        _builder.append(_value);
        _builder.newLineIfNotEmpty();
      }
    }
    return _builder;
  }

  public void processConstraint(final Constraint constraint) {
    if (constraint instanceof EClassifierConstraint) {
      _processConstraint((EClassifierConstraint)constraint);
      return;
    } else if (constraint instanceof TypeCheckConstraint) {
      _processConstraint((TypeCheckConstraint)constraint);
      return;
    } else if (constraint instanceof CheckConstraint) {
      _processConstraint((CheckConstraint)constraint);
      return;
    } else if (constraint instanceof CompareConstraint) {
      _processConstraint((CompareConstraint)constraint);
      return;
    } else if (constraint instanceof PathExpressionConstraint) {
      _processConstraint((PathExpressionConstraint)constraint);
      return;
    } else if (constraint instanceof PatternCompositionConstraint) {
      _processConstraint((PatternCompositionConstraint)constraint);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(constraint).toString());
    }
  }

  public void processCompositionCall(final CallableRelation call) {
    if (call instanceof EClassifierConstraint) {
      _processCompositionCall((EClassifierConstraint)call);
      return;
    } else if (call instanceof TypeCheckConstraint) {
      _processCompositionCall((TypeCheckConstraint)call);
      return;
    } else if (call instanceof PathExpressionConstraint) {
      _processCompositionCall((PathExpressionConstraint)call);
      return;
    } else if (call instanceof PatternCall) {
      _processCompositionCall((PatternCall)call);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(call).toString());
    }
  }

  public void processValue(final ValueReference value) {
    if (value instanceof AggregatedValue) {
      _processValue((AggregatedValue)value);
      return;
    } else if (value instanceof BoolValue) {
      _processValue((BoolValue)value);
      return;
    } else if (value instanceof FunctionEvaluationValue) {
      _processValue((FunctionEvaluationValue)value);
      return;
    } else if (value instanceof ListValue) {
      _processValue((ListValue)value);
      return;
    } else if (value instanceof NumberValue) {
      _processValue((NumberValue)value);
      return;
    } else if (value instanceof StringValue) {
      _processValue((StringValue)value);
      return;
    } else if (value instanceof EnumValue) {
      _processValue((EnumValue)value);
      return;
    } else if (value instanceof JavaConstantValue) {
      _processValue((JavaConstantValue)value);
      return;
    } else if (value instanceof VariableReference) {
      _processValue((VariableReference)value);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(value).toString());
    }
  }

  public void processCallAgg(final CallableRelation call) {
    if (call instanceof EClassifierConstraint) {
      _processCallAgg((EClassifierConstraint)call);
      return;
    } else if (call instanceof TypeCheckConstraint) {
      _processCallAgg((TypeCheckConstraint)call);
      return;
    } else if (call instanceof PathExpressionConstraint) {
      _processCallAgg((PathExpressionConstraint)call);
      return;
    } else if (call instanceof PatternCall) {
      _processCallAgg((PatternCall)call);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(call).toString());
    }
  }

  public void processVariable(final Variable variable) {
    if (variable instanceof LocalVariable) {
      _processVariable((LocalVariable)variable);
      return;
    } else if (variable instanceof Parameter) {
      _processVariable((Parameter)variable);
      return;
    } else if (variable instanceof ParameterRef) {
      _processVariable((ParameterRef)variable);
      return;
    } else if (variable != null) {
      _processVariable(variable);
      return;
    } else {
      throw new IllegalArgumentException("Unhandled parameter types: " +
        Arrays.<Object>asList(variable).toString());
    }
  }
}
