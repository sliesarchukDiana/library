package com.ascariaa.library.domain.mapper;

import com.ascariaa.library.domain.dto.BorrowRecordDto;
import com.ascariaa.library.domain.entity.Book;
import com.ascariaa.library.domain.entity.BorrowRecord;
import com.ascariaa.library.domain.enums.BorrowStatus;
import com.ascariaa.library.domain.mapper.BorrowRecordMapper;
import com.ascariaa.library.domain.mapper.BorrowRecordMapperImpl;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class BorrowRecordMapperTest {

    private final BorrowRecordMapper mapper = new BorrowRecordMapperImpl();

    @Test
    void toDto_validEntity_mapsFieldsCorrectly() {
        Book book = Book.builder().id(10L).title("1984").build();
        LocalDateTime now = LocalDateTime.now();
        BorrowRecord record = BorrowRecord.builder()
                .id(1L)
                .book(book)
                .borrowDate(now)
                .status(BorrowStatus.ACTIVE)
                .build();

        BorrowRecordDto dto = mapper.toDto(record);

        assertEquals(1L, dto.getId());
        assertEquals(10L, dto.getBookId());
        assertEquals("1984", dto.getBookTitle());
        assertEquals(now, dto.getBorrowDate());
        assertEquals(BorrowStatus.ACTIVE, dto.getStatus());
    }

    @Test
    void toDto_nullEntity_returnsNull() {
        assertNull(mapper.toDto(null));
    }
}