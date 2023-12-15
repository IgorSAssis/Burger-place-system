CREATE TABLE reviews (
    id              SERIAL      PRIMARY KEY,
    grade           INT         NOT NULL,
    comment         VARCHAR(255),
    occupation_id   BIGINT      NOT NULL,
    FOREIGN KEY (occupation_id) REFERENCES occupations(id)
);
