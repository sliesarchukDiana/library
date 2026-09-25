package com.ascariaa.library.mapper;

import com.ascariaa.library.dto.BookCreateDto;
import com.ascariaa.library.dto.BookDto;
import com.ascariaa.library.entity.Book;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-24T21:51:00+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 26.0.2 (Oracle Corporation)"
)
@Component
public class BookMapperImpl implements BookMapper {

    @Override
    public BookDto toDto(Book book) {
        if ( book == null ) {
            return null;
        }

        BookDto bookDto = new BookDto();

        bookDto.setId( book.getId() );
        bookDto.setTitle( book.getTitle() );
        bookDto.setAuthor( book.getAuthor() );
        bookDto.setAvailableCopies( book.getAvailableCopies() );

        return bookDto;
    }

    @Override
    public Book toEntity(BookCreateDto dto) {
        if ( dto == null ) {
            return null;
        }

        Book.BookBuilder book = Book.builder();

        book.title( dto.getTitle() );
        book.author( dto.getAuthor() );
        book.availableCopies( dto.getAvailableCopies() );

        return book.build();
    }

    @Override
    public void updateEntity(Book book, BookCreateDto dto) {
        if ( dto == null ) {
            return;
        }

        book.setTitle( dto.getTitle() );
        book.setAuthor( dto.getAuthor() );
        book.setAvailableCopies( dto.getAvailableCopies() );
    }
}
