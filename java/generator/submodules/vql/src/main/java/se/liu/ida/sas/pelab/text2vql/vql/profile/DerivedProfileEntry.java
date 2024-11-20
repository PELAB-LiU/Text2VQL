package se.liu.ida.sas.pelab.text2vql.vql.profile;

import static se.liu.ida.sas.pelab.text2vql.vql.profile.ProfileEntry.*;

public enum DerivedProfileEntry {
    AUX("auxiliaries", (Database db) -> {
        return db.get(PATTERN) - db.get(MAIN);
    }),
    OR("ors", (Database db) -> {
        return db.get(BODIES) - db.get(PATTERN);
    }),
    CONSTRAINT("ors", (Database db) -> {
        return db.get(BODIES) - db.get(PATTERN);
    }),
    COMPARE("ors", (Database db) -> {
        return db.get(EQ) + db.get(NEQ);
    }),
    COMPOSITION("ors", (Database db) -> {
        return db.get(PCOMP) + db.get(NCOMP);
    });


    public final String label;
    public final Operation operation;
    private DerivedProfileEntry(String label, Operation operation) {
        this.label = label;
        this.operation = operation;
    }
    public interface Operation{
        int get(Database db);
    }
}
