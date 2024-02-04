CREATE TABLE occupations (
    id                UUID           PRIMARY KEY,
    begin_occupation  TIMESTAMP,
    end_occupation    TIMESTAMP,
    payment_form      VARCHAR(255),
    people_count      INTEGER,
    board_id          UUID            NOT NULL,
    active            BOOLEAN         NOT NULL,

    FOREIGN KEY (board_id) REFERENCES boards (id)
);