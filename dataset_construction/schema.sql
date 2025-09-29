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


CREATE TABLE chatgpt (
	id INTEGER PRIMARY KEY autoincrement,
	metamodel TEXT NOT NULL,
    lang TEXT NOT NULL,
    feat TEXT NOT NULL,
    descript TEXT NOT NULL,
    signat,
	pattern TEXT NOT NULL,
    generation INTEGER NOT NULL,
	syntax BOOLEAN,
	FOREIGN KEY (metamodel) REFERENCES metamodels (model)
);

CREATE TABLE mutation (
	query INTEGER NOT NULL,
    parent INTEGER NOT NULL,
    PRIMARY KEY (query, parent),
    FOREIGN KEY (query) REFERENCES chatgpt(id),
    FOREIGN KEY (parent) REFERENCES chatgpt(id)
);
