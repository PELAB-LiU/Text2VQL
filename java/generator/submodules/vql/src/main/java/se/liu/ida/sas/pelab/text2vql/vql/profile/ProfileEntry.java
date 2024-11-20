package se.liu.ida.sas.pelab.text2vql.vql.profile;

public enum ProfileEntry {
    MAIN("main"),
    PATTERN("patterns"),
    BODIES("bodies"),
    MODS("modifiers"),
    EQ("equal"),
    NEQ("not equal"),
    CHECK("check"),
    PCOMP("positive composition"),
    NCOMP("negative composition"),
    PATH("path"),
    TYPE("type"),
    CLASSIFIER("classifier"),
    PATTERNCALL("patterncall"),
    ENUM("enum value"),
    JAVA("java value"),
    VARIABLEREF("varaible reference"),
    FVAL("function value"),
    AVAL("aggregate value"),
    STRING("string"),
    NUMBER("number"),
    BOOL("boolean"),
    LIST("list"),
    TRANSITIVE("transitive closure"),
    REFLEXIVE("reflexive closure"),
    LVAR("local variable"),
    PVAR("paremeter"),
    PRVAR("parameter reference variable");
    public final String label;

    private ProfileEntry(String label) {
        this.label = label;
    }
}
