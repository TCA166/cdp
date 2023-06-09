DROP TABLE IF EXISTS games;
DROP TABLE IF EXISTS keys;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS owners;

CREATE TABLE "games" (
	"uid"	INTEGER NOT NULL UNIQUE,
	"name"	TEXT NOT NULL,
	"date"	TEXT,
	"studio"	TEXT,
	PRIMARY KEY("uid" AUTOINCREMENT)
);

CREATE TABLE "keys" (
	"uid"	INTEGER NOT NULL UNIQUE,
	"key"	BLOB NOT NULL UNIQUE,
	"expiry"	TEXT,
	"admin"	INTEGER NOT NULL,
	"owner"	INTEGER,
	FOREIGN KEY("owner") REFERENCES "users"("uid"),
	PRIMARY KEY("uid")
)

CREATE TABLE "users" (
	"uid"	INTEGER NOT NULL UNIQUE,
	"login"	TEXT NOT NULL UNIQUE,
	"salt"	BLOB NOT NULL,
	"pass"	BLOB NOT NULL,
	"admin"	INTEGER NOT NULL,
	PRIMARY KEY("uid")
);

CREATE TABLE "owners" (
	"user"	INTEGER NOT NULL,
	"game"	INTEGER NOT NULL,
	FOREIGN KEY("game") REFERENCES "games"("uid"),
	FOREIGN KEY("user") REFERENCES "users"("uid")
);