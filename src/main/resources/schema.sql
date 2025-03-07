DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;


CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(512) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS items (
    item_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR NOT NULL,
    available BOOLEAN NOT NULL,
    owner BIGINT REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS bookings (
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    item_id BIGINT REFERENCES items (item_id) NOT NULL,
    booker_id BIGINT REFERENCES users (user_id) NOT NULL,
    status VARCHAR
);

CREATE TABLE IF NOT EXISTS requests (
    request_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description VARCHAR NOT NULL,
    requestor BIGINT REFERENCES users (user_id) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR NOT NULL,
    item_id BIGINT REFERENCES items (item_id) NOT NULL,
    author_id BIGINT REFERENCES users (user_id) NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL
);
