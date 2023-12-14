CREATE TABLE orders (
    id              SERIAL          PRIMARY KEY,
    status          VARCHAR(255),
    opened_at       TIMESTAMP,
    closed_at       TIMESTAMP,
    payment_form    VARCHAR(255),
    customer_id     BIGINT          NOT NULL,
    active          BOOLEAN         NOT NULL,
    FOREIGN KEY (customer_id) REFERENCES customers (id)
);