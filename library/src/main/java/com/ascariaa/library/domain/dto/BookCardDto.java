package com.ascariaa.library.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookCardDto {
    private byte[] receiptPdf;
    private String rating;
}