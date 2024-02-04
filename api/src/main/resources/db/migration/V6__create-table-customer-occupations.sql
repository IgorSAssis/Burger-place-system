CREATE TABLE customer_occupations (
    id              UUID    PRIMARY KEY,
    customer_id     UUID    NOT NULL,
    occupation_id   UUID    NOT NULL,
    CONSTRAINT fk_customer_customer_occupations     FOREIGN KEY (customer_id)   REFERENCES customers (id),
    CONSTRAINT fk_occupation_customer_occupations   FOREIGN KEY (occupation_id) REFERENCES occupations (id)
);