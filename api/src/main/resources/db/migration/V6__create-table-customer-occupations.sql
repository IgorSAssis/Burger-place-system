CREATE TABLE customer_occupations (
    id              SERIAL  PRIMARY KEY,
    customer_id     BIGINT  NOT NULL,
    occupation_id   BIGINT  NOT NULL,
    CONSTRAINT fk_customer_customer_occupations     FOREIGN KEY (customer_id)   REFERENCES customers (id),
    CONSTRAINT fk_occupation_customer_occupations   FOREIGN KEY (occupation_id) REFERENCES occupations (id)
);