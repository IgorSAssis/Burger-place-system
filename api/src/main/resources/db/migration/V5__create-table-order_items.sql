CREATE TABLE order_items (
    id              SERIAL              PRIMARY KEY,
    amount          INT                 NOT NULL,
    item_value      DOUBLE PRECISION    NOT NULL,
    status          VARCHAR(100)        NOT NULL,
    observation     VARCHAR(200),
    product_id      BIGINT              NOT NULL,
    occupation_id   BIGINT              NOT NULL,
    active          BOOLEAN             NOT NULL,
    FOREIGN KEY (product_id)        REFERENCES products (id),
    FOREIGN KEY (occupation_id)     REFERENCES occupations (id)
);
