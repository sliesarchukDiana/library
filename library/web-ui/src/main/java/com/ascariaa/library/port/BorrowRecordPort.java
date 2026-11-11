package com.ascariaa.library.port;

import com.ascariaa.library.domain.dto.BorrowRecordDto;
import java.util.List;

public interface BorrowRecordPort {
    List<BorrowRecordDto> getActiveRecords(String token);
    void borrowBook(Long bookId, String token);
    void returnBook(Long recordId, String token);
    void generateReport(String token);
}