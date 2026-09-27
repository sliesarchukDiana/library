package com.ascariaa.library.domain.dto;

import lombok.Data;

@Data
public class BookDto {
    private Long id;
    private String title;
    private String author;
    private Integer availableCopies;
}