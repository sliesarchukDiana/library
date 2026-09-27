package com.ascariaa.library.domain.dto;

import com.ascariaa.library.domain.annotation.ValidBook;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@ValidBook
public class BookCreateDto {
    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Author cannot be blank")
    private String author;

    @NotNull(message = "Available copies must be specified")
    @Min(value = 0, message = "Available copies cannot be negative")
    private Integer availableCopies;
}