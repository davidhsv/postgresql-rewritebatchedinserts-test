-- DDL for the Post entity
CREATE SEQUENCE post_seq;

CREATE TABLE post (
                      id BIGINT DEFAULT nextval('post_seq') PRIMARY KEY,
                      title VARCHAR(255) NOT NULL
);
