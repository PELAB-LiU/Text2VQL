CREATE TABLE metamodels (
	id TEXT PRIMARY KEY,
	path TEXT NOT NULL,
	domain TEXT NOT NULL
);

CREATE TABLE pairs (
	id integer primary key autoincrement,
	nl TEXT NOT NULL,
	pattern TEXT NOT NULL,
	metamodel TEXT NOT NULL,
	FOREIGN KEY (metamodel)
    REFERENCES metamodels (id)
);
