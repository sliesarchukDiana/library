CREATE TABLE book (
                      id BIGSERIAL PRIMARY KEY,
                      title VARCHAR(255) NOT NULL,
                      author VARCHAR(255) NOT NULL,
                      available_copies INT NOT NULL CHECK (available_copies >= 0)
);