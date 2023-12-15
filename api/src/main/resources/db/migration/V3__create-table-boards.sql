CREATE TABLE boards (
    id          SERIAL                      PRIMARY KEY,
    capacity    INTEGER         NOT NULL,
    number      INTEGER         NOT NULL    UNIQUE,
    location    VARCHAR(100)    NOT NULL
);