CREATE TABLE evaluation (
	id INTEGER PRIMARY KEY autoincrement,
	llm TEXT NOT NULL,
    finetune BOOLEAN NOT NULL,
    caseid INTEGER NOT NULL, 
    domain TEXT NOT NULL,
    lang TEXT NOT NULL,
    shotid INTEGER NOT NULL, 
    query TEXT NOT NULL, 

    syntax BOOLEAN,
    diagnostics TEXT,
    semantics BOOLEAN,
    idicator TEXT
);