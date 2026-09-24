CREATE TABLE book (
        id BIGSERIAL PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        author VARCHAR(255) NOT NULL,
        available_copies INT NOT NULL CHECK (available_copies >= 0)
);

CREATE TABLE borrow_record (
        id BIGSERIAL PRIMARY KEY,
        book_id BIGINT NOT NULL,
        keycloak_user_id UUID NOT NULL,
        borrow_date TIMESTAMP NOT NULL,
        return_date TIMESTAMP,
        status VARCHAR(20) NOT NULL,
        CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES book(id)
);