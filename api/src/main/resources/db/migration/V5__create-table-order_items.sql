CREATE TABLE order_items (
    id              UUID                PRIMARY KEY,
    amount          INT                 NOT NULL,
    item_value      DOUBLE PRECISION    NOT NULL,
    status          VARCHAR(100)        NOT NULL,
    observation     VARCHAR(200),
    product_id      UUID                NOT NULL,
    occupation_id   UUID                NOT NULL,
    active          BOOLEAN             NOT NULL,
    FOREIGN KEY (product_id)        REFERENCES products (id),
    FOREIGN KEY (occupation_id)     REFERENCES occupations (id)
);
