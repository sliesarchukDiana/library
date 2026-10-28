package com.ascariaa.library.domain.validation;

import com.ascariaa.library.domain.annotation.ValidBook;
import com.ascariaa.library.domain.dto.BookCreateDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidBookValidator implements ConstraintValidator<ValidBook, BookCreateDto> {
    @Override
    public boolean isValid(BookCreateDto dto, ConstraintValidatorContext context) {
        if (dto.getTitle() == null || dto.getAuthor() == null) {
            return true;
        }
        boolean isValid = !dto.getTitle().trim().equalsIgnoreCase(dto.getAuthor().trim());

        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Book title and author's name cannot be identical")
                    .addPropertyNode("title")
                    .addConstraintViolation();
        }
        return isValid;
    }
}