CREATE TABLE products (
    id          SERIAL              PRIMARY KEY,
    name        VARCHAR(255)        NOT NULL,
    ingredients VARCHAR(255)        NOT NULL,
    price       DOUBLE PRECISION    NOT NULL,
    category    VARCHAR(100)        NOT NULL,
    url         VARCHAR(255),
    active      BOOLEAN             NOT NULL
);
