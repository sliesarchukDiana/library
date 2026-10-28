package com.ascariaa.library.domain.mapper;

import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.domain.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BookMapper {

    BookDto toDto(Book book);

    Book toEntity(BookCreateDto dto);

    void updateEntity(@MappingTarget Book book, BookCreateDto dto);
}