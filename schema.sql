CREATE TABLE metamodels (
	id TEXT PRIMARY KEY,
	dataset TEXT NOT NULL,
	definition TEXT NOT NULL,
	elements INTEGER NOT NULL
);

CREATE TABLE pairs (
	id integer primary key autoincrement,
	nl TEXT NOT NULL,
	pattern TEXT NOT NULL,
	metamodel TEXT NOT NULL,
	FOREIGN KEY (metamodel)
    REFERENCES metamodels (id)
);

CREATE TABLE similarities (
    id integer primary key autoincrement,
    m1 integer NOT NULL,
    m2 integer NOT NULL,
    FOREIGN KEY (m1)
    REFERENCES metamodels (id),
    FOREIGN KEY (m2)
    REFERENCES metamodels (id)
);

CREATE TABLE representatives (
    id TEXT PRIMARY KEY,
    FOREIGN KEY (id)
    REFERENCES metamodels (id)
);