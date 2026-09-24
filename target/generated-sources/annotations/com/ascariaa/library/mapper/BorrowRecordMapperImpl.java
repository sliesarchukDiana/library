package com.ascariaa.library.mapper;

import com.ascariaa.library.dto.BorrowRecordDto;
import com.ascariaa.library.entity.Book;
import com.ascariaa.library.entity.BorrowRecord;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-24T20:02:58+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Oracle Corporation)"
)
@Component
public class BorrowRecordMapperImpl implements BorrowRecordMapper {

    @Override
    public BorrowRecordDto toDto(BorrowRecord record) {
        if ( record == null ) {
            return null;
        }

        BorrowRecordDto borrowRecordDto = new BorrowRecordDto();

        borrowRecordDto.setBookId( recordBookId( record ) );
        borrowRecordDto.setBookTitle( recordBookTitle( record ) );
        borrowRecordDto.setId( record.getId() );
        borrowRecordDto.setBorrowDate( record.getBorrowDate() );
        borrowRecordDto.setReturnDate( record.getReturnDate() );
        borrowRecordDto.setStatus( record.getStatus() );

        return borrowRecordDto;
    }

    private Long recordBookId(BorrowRecord borrowRecord) {
        Book book = borrowRecord.getBook();
        if ( book == null ) {
            return null;
        }
        return book.getId();
    }

    private String recordBookTitle(BorrowRecord borrowRecord) {
        Book book = borrowRecord.getBook();
        if ( book == null ) {
            return null;
        }
        return book.getTitle();
    }
}
