DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS bookings CASCADE;
DROP TABLE IF EXISTS item_request CASCADE;
DROP TABLE IF EXISTS comments CASCADE;

CREATE TABLE IF NOT EXISTS users (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                     name VARCHAR(255)                          NOT NULL,
                                     email VARCHAR(512)                         NOT NULL,
                                     CONSTRAINT PK_USER PRIMARY KEY (id),
                                     CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
                                     id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                     name VARCHAR (255)                         NOT NULL,
                                     description VARCHAR(512)                   NOT NULL,
                                     available BOOLEAN,
                                     owner_id BIGINT                            NOT NULL,
                                     request_id BIGINT,
                                     CONSTRAINT PK_ITEM PRIMARY KEY (id),
                                     CONSTRAINT FK_ITEM_FOR_OWNER FOREIGN KEY (owner_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings (
                                        id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                        start_date TIMESTAMP WITHOUT TIME ZONE,
                                        end_date TIMESTAMP WITHOUT TIME ZONE,
                                        item_id BIGINT                             NOT NULL,
                                        booker_id BIGINT                           NOT NULL,
                                        status VARCHAR (25),
                                        CONSTRAINT PK_BOOKING PRIMARY KEY (id),
                                        CONSTRAINT FK_BOOKING_FOR_BOOKER FOREIGN KEY (booker_id) REFERENCES users (id),
                                        CONSTRAINT FK_BOOKING_FOR_ITEM FOREIGN KEY (item_id) REFERENCES items (id)
);

CREATE TABLE IF NOT EXISTS item_request (
                                            id BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                            description VARCHAR(512)                   NOT NULL,
                                            requester_id BIGINT,
                                            created date                               NOT NULL,
                                            CONSTRAINT PK_ITEM_REQUEST PRIMARY KEY (id),
                                            CONSTRAINT FK_ITEM_REQUEST_FOR_REQUESTER FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS comments (
                                        id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
                                        text      VARCHAR(1024)                           NOT NULL,
                                        item_id   BIGINT                                  NOT NULL,
                                        author_id BIGINT                                  NOT NULL,
                                        created   TIMESTAMP WITHOUT TIME ZONE             NOT NULL,
                                        CONSTRAINT PK_COMMENTS PRIMARY KEY (id),
                                        CONSTRAINT FK_COMMENT_FOR_ITEM FOREIGN KEY (item_id) REFERENCES items (id),
                                        CONSTRAINT FK_COMMENT_FOR_USER FOREIGN KEY (author_id) REFERENCES users (id)
);