-- Definitions for the database tables in an easy to read format.

CREATE TABLE Party (
    pid INTEGER UNIQUE,
    name TEXT NOT NULL,
    mid INTEGER NOT NULL,
    vid INTEGER NOT NULL,
    eid INTEGER NOT NULL,
    price NUMERIC NOT NULL,
    timing TIMESTAMP NOT NULL,
    numberofguests INTEGER NOT NULL,
    PRIMARY KEY (pid),
    FOREIGN KEY (mid) REFERENCES Menu(mid)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    FOREIGN KEY (vid) REFERENCES Venue(vid)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    FOREIGN KEY (eid) REFERENCES Entertainment(eid)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
    CHECK (price >= 0),
    CHECK (numberofguests >= 0)
)

CREATE TABLE Venue (
    vid INTEGER UNIQUE,
    name TEXT NOT NULL,
    venuecost NUMERIC NOT NULL,
    PRIMARY KEY (vid),
    CHECK (venuecost >= 0)
)

CREATE TABLE Menu (
    mid INTEGER UNIQUE,
    description TEXT NOT NULL,
    costprice NUMERIC NOT NULL,
    PRIMARY KEY (mid),
    CHECK (costprice >= 0)
)

CREATE TABLE Entertainment (
    eid INTEGER UNIQUE,
    description TEXT NOT NULL,
    costprice NUMERIC NOT NULL,
    PRIMARY KEY (eid),
    CHECK (costprice >= 0)
);