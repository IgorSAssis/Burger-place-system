CREATE TABLE boards (
    id          UUID                        PRIMARY KEY,
    capacity    INTEGER         NOT NULL,
    number      INTEGER         NOT NULL    UNIQUE,
    location    VARCHAR(100)    NOT NULL,
    occupied    BOOLEAN         NOT NULL,
    active      BOOLEAN         NOT NULL
);