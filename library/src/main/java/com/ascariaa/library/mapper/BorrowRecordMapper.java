package com.ascariaa.library.mapper;

import com.ascariaa.library.dto.BorrowRecordDto;
import com.ascariaa.library.entity.BorrowRecord;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface BorrowRecordMapper {

    @Mapping(source = "book.id", target = "bookId")
    @Mapping(source = "book.title", target = "bookTitle")
    BorrowRecordDto toDto(BorrowRecord record);
}