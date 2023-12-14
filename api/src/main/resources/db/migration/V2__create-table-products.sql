CREATE TABLE products (
    id          SERIAL              PRIMARY KEY,
    name        VARCHAR(255)        NOT NULL,
    price       DOUBLE PRECISION    NOT NULL,
    active      BOOLEAN             NOT NULL,
    description VARCHAR(255)
);
