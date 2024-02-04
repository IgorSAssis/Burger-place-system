CREATE TABLE topic_reviews (
    id              UUID          PRIMARY KEY,
    grade           INT           NOT NULL,
    category        VARCHAR(20)   NOT NULL,
    review_id       UUID,

    FOREIGN KEY (review_id)   REFERENCES reviews(id)
);
