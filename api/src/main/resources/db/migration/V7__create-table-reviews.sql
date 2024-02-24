CREATE TABLE reviews (
    id              UUID          PRIMARY KEY,
    comment         VARCHAR(255),
    occupation_id   UUID          NOT NULL,

    FOREIGN KEY (occupation_id) REFERENCES occupations(id)
);
