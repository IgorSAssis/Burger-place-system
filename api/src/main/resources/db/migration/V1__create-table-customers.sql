CREATE TABLE customers (
    id                  SERIAL          PRIMARY KEY,
    name                VARCHAR(255)    NOT NULL,
    email               VARCHAR(255)    NOT NULL UNIQUE,
    cpf                 VARCHAR(11)     NOT NULL UNIQUE,
    street_address      VARCHAR(255)    NOT NULL,
    neighborhood        VARCHAR(50)     NOT NULL,
    city                VARCHAR(50)     NOT NULL,
    state               VARCHAR(50)     NOT NULL,
    postal_code         VARCHAR(8)      NOT NULL,
    residential_number  VARCHAR(20),
    complement          VARCHAR(60),
    active              BOOLEAN         NOT NULL
);
