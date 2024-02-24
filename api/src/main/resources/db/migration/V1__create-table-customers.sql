CREATE TABLE customers (
    id                  UUID            PRIMARY KEY,
    name                VARCHAR(255)    NOT NULL,
    email               VARCHAR(255)    NOT NULL UNIQUE,
    cpf                 VARCHAR(11)     NOT NULL UNIQUE,
    active              BOOLEAN         NOT NULL
);
