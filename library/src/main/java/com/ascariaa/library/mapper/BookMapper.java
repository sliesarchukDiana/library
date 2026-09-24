package com.ascariaa.library.mapper;

import com.ascariaa.library.dto.BookCreateDto;
import com.ascariaa.library.dto.BookDto;
import com.ascariaa.library.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    BookDto toDto(Book book);

    Book toEntity(BookCreateDto dto);

    void updateEntity(@MappingTarget Book book, BookCreateDto dto);
}