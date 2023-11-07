from text2vql.metamodel import MetaModel

SEED_METAMODEL_RELATIONAL = MetaModel('seed_metamodels/relational.ecore')

NL1 = "All databases whose names are Shop"
QUERY1 = """pattern databaseNameShop(db : Database) {
	Database.name(db, "Shop");
}"""

NL4 = "All tables whose names are countries"
QUERY4 = """pattern databaseNameShop(t : Table) {
	Table.name(db, "countries");
}"""

NL2 = "Retrieve all columns whose attributes are dates"
QUERY2 = """pattern columnsDates(c : Column) {
	Column.type(c, Type::DATE);
}"""

NL5 = "Retrieve all columns whose attributes are strings"
QUERY5 = """pattern columnsDates(c : Column) {
	Column.type(c, Type::VARCHAR);
}"""


NL3 = "Fks whose comments start with 'This' and retrieve their comments"
QUERY3 = """pattern modelElementsCommentsThis(me : ModelElement, c : EString) {
    ForeignKey(me);
    ModelElement.comment(me, c);
    check(c.startsWith("This"));
}"""

RELATIONAL_PAIRS = [(QUERY1, NL1), (QUERY2, NL2), (QUERY3, NL3), (QUERY4, NL4), (QUERY5, NL5)]
SEED_RELATIONAL_SAMPLE = (SEED_METAMODEL_RELATIONAL, RELATIONAL_PAIRS)
