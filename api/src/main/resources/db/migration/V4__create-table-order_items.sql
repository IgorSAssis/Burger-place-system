CREATE TABLE order_items (
    id              SERIAL              PRIMARY KEY,
    qtd_itens       INT                 NOT NULL,
    item_value      DOUBLE PRECISION    NOT NULL,
    product_id      BIGINT              NOT NULL,
    order_id        BIGINT              NOT NULL,
    active          BOOLEAN             NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products (id),
    FOREIGN KEY (order_id)   REFERENCES orders (id)
);
