CREATE TABLE metamodels (
	model TEXT PRIMARY KEY,
	dataset TEXT NOT NULL,
    parseable BOOLEAN NOT NULL,
	definition TEXT NOT NULL DEFAULT '',
	elements INTEGER NOT NULL DEFAULT -1
);

CREATE TABLE similarities (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    m1 INTEGER NOT NULL,
    m2 INTEGER NOT NULL,
    similarity REAL NOT NULL DEFAULT 0,
FOREIGN KEY (m1) REFERENCES metamodels(model),
    FOREIGN KEY (m2) REFERENCES metamodels(model)
);

CREATE TABLE clusters (
    model TEXT PRIMARY KEY,
    cluster integer NOT NULL,
    FOREIGN KEY (model) REFERENCES metamodels (model)
)

CREATE TABLE samples (
    model TEXT PRIMARY KEY,
    cluster integer NOT NULL,
    FOREIGN KEY (model) REFERENCES metamodels (model)
)

CREATE TABLE domains (
    model TEXT PRIMARY KEY,
    compiled BOOLEAN NOT NULL,
    FOREIGN KEY (model) REFERENCES metamodels (model)
)



CREATE TABLE pairs (
	id integer primary key autoincrement,
	nl TEXT NOT NULL,
	pattern TEXT NOT NULL,
	metamodel TEXT NOT NULL,
	FOREIGN KEY (metamodel)
    REFERENCES metamodels (id)
);
