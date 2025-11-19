package se.liu.ida.sas.pelab.text2vql.comparison.input;

public enum EvaluationResult {
    SYNTAX_ERROR,
    PARSED,
    SEMANTIC_ERROR,
    NULLPOINTER,
    CORRECT;

    public boolean canProceedWithEvaluation(){
        switch (this) {
            case SYNTAX_ERROR: return false;
            case PARSED: return true;
            case SEMANTIC_ERROR: return false;
            case NULLPOINTER: return true; //Null pointers are considered a lesser case of erroneous
            case CORRECT: return true;
        }
        throw new RuntimeException("Not all cases are handled.");
    }

    public EvaluationResult update(boolean hasSemanticError){
        switch (this) {
            case SYNTAX_ERROR: return SYNTAX_ERROR;
            case PARSED: return hasSemanticError ? SEMANTIC_ERROR : PARSED;
            case SEMANTIC_ERROR: return SEMANTIC_ERROR;
            case NULLPOINTER: return hasSemanticError ? SEMANTIC_ERROR : NULLPOINTER;
            case CORRECT: return hasSemanticError ? SEMANTIC_ERROR : CORRECT;
        }
        throw new RuntimeException("Not all cases are handled.");
    }

    public EvaluationResult assessment(){
        switch (this) {
            case SYNTAX_ERROR: return SYNTAX_ERROR;
            case PARSED: return CORRECT;
            case SEMANTIC_ERROR: return SEMANTIC_ERROR;
            case NULLPOINTER: return NULLPOINTER;
            case CORRECT: return CORRECT;
        }
        throw new RuntimeException("Not all cases are handled.");
    }
}
