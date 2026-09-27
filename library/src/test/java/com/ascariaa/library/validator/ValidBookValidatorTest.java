package com.ascariaa.library.validator;

import com.ascariaa.library.domain.dto.BookCreateDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ValidBookValidatorTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            validator = factory.getValidator();
        }
    }

    @Test
    void validate_titleAndAuthorAreDifferent_noViolations() {
        BookCreateDto dto = new BookCreateDto();
        dto.setTitle("1984");
        dto.setAuthor("George Orwell");
        dto.setAvailableCopies(5);

        Set<ConstraintViolation<BookCreateDto>> violations = validator.validate(dto);

        assertTrue(violations.isEmpty(), "No violations found");
    }

    @Test
    void validate_titleAndAuthorAreIdentical_returnsViolationWithMessage() {
        BookCreateDto dto = new BookCreateDto();
        dto.setTitle("George Orwell");
        dto.setAuthor("George Orwell");
        dto.setAvailableCopies(5);

        Set<ConstraintViolation<BookCreateDto>> violations = validator.validate(dto);

        assertFalse(violations.isEmpty(), "Error expected");
        ConstraintViolation<BookCreateDto> violation = violations.iterator().next();

        assertEquals("Book title and author's name cannot be identical", violation.getMessage());
        assertEquals("title", violation.getPropertyPath().toString());
    }
}