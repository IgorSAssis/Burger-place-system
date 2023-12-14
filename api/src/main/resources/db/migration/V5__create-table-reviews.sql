CREATE TABLE reviews (
    id          SERIAL  PRIMARY KEY,
    grade       INT     NOT NULL,
    comment     VARCHAR(255),
    order_id    BIGINT  NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);
