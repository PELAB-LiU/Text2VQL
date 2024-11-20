package se.liu.ida.sas.pelab.text2vql.vql.profile;

import org.eclipse.viatra.query.patternlanguage.emf.util.PatternParsingResults;
import org.eclipse.viatra.query.patternlanguage.emf.vql.*;

import static se.liu.ida.sas.pelab.text2vql.vql.profile.ProfileEntry.*;

public class QueryProfile {
    private Database db = new Database();
    
    public void processParsed(PatternParsingResults parsed){
        db.increment(MAIN);
        parsed.getPatterns().forEach(this::processPattern);
    }
    public void processPattern(Pattern pattern){
        db.increment(PATTERN);
        if(pattern.getModifiers().isPrivate() || pattern.getModifiers().getExecution()!=ExecutionType.UNSPECIFIED){
            db.increment(MODS);
        }
        pattern.getBodies().forEach(this::processBody);
        pattern.getParameters().forEach(this::processVariable);
    }
    public void processBody(PatternBody body){
        db.increment(BODIES);
        body.getConstraints().forEach(this::processConstraint);
        body.getVariables().forEach(this::processVariable);
    }

    public void processConstraint(Constraint constraint){
        switch (constraint){
            case CompareConstraint cc -> {
                switch (cc.getFeature()){
                    case EQUALITY -> db.increment(EQ);
                    case INEQUALITY -> db.increment(NEQ);
                }
                processValue(cc.getLeftOperand());
                processValue(cc.getRightOperand());
            }
            case CheckConstraint cc -> db.increment(CHECK);
            case PatternCompositionConstraint pcc -> {
                if(pcc.isNegative()){
                    db.increment(NCOMP);
                } else {
                    db.increment(PCOMP);
                }
                processCompositionCall(pcc.getCall());
            }
            case PathExpressionConstraint pec -> {
                db.increment(PATH);

                pec.getSourceType();
                pec.getEdgeTypes();
                pec.getTransitive();

                processValue(pec.getSrc());
                processValue(pec.getDst());
            }
            case TypeCheckConstraint tcc -> {
                db.increment(TYPE);
                tcc.getTransitive();
                tcc.getType();
                processValue(tcc.getVar());
            }
            case EClassifierConstraint ecc -> {
                db.increment(CLASSIFIER);
                ecc.getTransitive();
                ecc.getType();
                processValue(ecc.getVar());
            }
            default -> throw new IllegalStateException("Unexpected value: " + constraint.getClass());
        }
    }
    public void processCompositionCall(CallableRelation cr){
        switch (cr){
            case PathExpressionConstraint pec -> {
                processConstraint(pec);
                processClosure(pec.getTransitive());
            }
            case TypeCheckConstraint tcc -> {
                processConstraint(tcc);
                processClosure(tcc.getTransitive());
            }
            case EClassifierConstraint ecc -> {
                processConstraint(ecc);
                processClosure(ecc.getTransitive());
            }
            case PatternCall pc -> {
                db.increment(PATTERNCALL);
                pc.getParameters().forEach(this::processValue);
                processClosure(pc.getTransitive());
            }
            default -> throw new IllegalStateException("Unexpected value: " + cr.getClass());
        }
    }
    public void processValue(ValueReference vr){
        switch (vr){
            case EnumValue ev -> db.increment(ENUM);
            case JavaConstantValue jcv -> db.increment(JAVA);
            case VariableReference vr2 -> {
                db.increment(VARIABLEREF);
                //TODO aggregator
            }
            case FunctionEvaluationValue fev -> db.increment(FVAL);
            case AggregatedValue av -> {
                db.increment(AVAL);
                //TODO get aggregator
            }
            case StringValue sv -> db.increment(STRING);
            case NumberValue nv -> db.increment(NUMBER);
            case BoolValue bv -> db.increment(BOOL);
            case ListValue lv -> db.increment(LIST);
            default -> throw new IllegalStateException("Unexpected value: " + vr.getClass());
        }
    }
    public void processClosure(ClosureType closureType){
        if(closureType == ClosureType.TRANSITIVE){
            db.increment(TRANSITIVE);
        }
        if(closureType == ClosureType.REFLEXIVE_TRANSITIVE){
            db.increment(REFLEXIVE);
        }
    }
    public void processVariable(Variable var){
        switch (var){
            case LocalVariable lv -> db.increment(LVAR);
            case Parameter p -> db.increment(PVAR);
            case ParameterRef pr -> db.increment(PRVAR);

            default -> throw new IllegalStateException("Unexpected value: " + var.getClass());
        }
    }
    public static String header(){
        return "TODO csv header";
    }
    public String data(){
        return "TODO print data as csv";
    }
}
