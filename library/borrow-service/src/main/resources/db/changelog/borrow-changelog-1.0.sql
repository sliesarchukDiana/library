CREATE TABLE borrow_record (
                               id BIGSERIAL PRIMARY KEY,
                               book_id BIGINT NOT NULL,
                               keycloak_user_id UUID NOT NULL,
                               borrow_date TIMESTAMP NOT NULL,
                               return_date TIMESTAMP,
                               status VARCHAR(20) NOT NULL,
                               reminder_sent_date TIMESTAMP
);