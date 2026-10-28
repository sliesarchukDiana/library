package com.ascariaa.library.domain.annotation;

import com.ascariaa.library.domain.validation.ValidBookValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidBookValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidBook {
    String message() default "Book title cannot be the author's name";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}