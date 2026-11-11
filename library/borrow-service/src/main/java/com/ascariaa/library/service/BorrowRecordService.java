package com.ascariaa.library.service;

import com.ascariaa.library.domain.dto.BorrowRecordDto;
import com.ascariaa.library.domain.entity.BorrowRecord;
import com.ascariaa.library.domain.enums.BorrowStatus;
import com.ascariaa.library.domain.mapper.BorrowRecordMapper;
import com.ascariaa.library.port.BookInventoryPort;
import com.ascariaa.library.repository.BorrowRecordRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BorrowRecordService {
    private final BorrowRecordRepository borrowRecordRepository;
    private final BorrowRecordMapper borrowRecordMapper;
    private final AsyncLibraryService asyncLibraryService;
    private final BookInventoryPort bookInventoryPort;

    public BorrowRecordService(BorrowRecordRepository borrowRecordRepository,
                               BorrowRecordMapper borrowRecordMapper,
                               AsyncLibraryService asyncLibraryService,
                               BookInventoryPort bookInventoryPort) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.borrowRecordMapper = borrowRecordMapper;
        this.asyncLibraryService = asyncLibraryService;
        this.bookInventoryPort = bookInventoryPort;
    }

    private String getCurrentToken() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof Jwt jwt) {
            return "Bearer " + jwt.getTokenValue();
        }
        return "";
    }

    @Transactional
    public BorrowRecordDto borrowBook(Long bookId, UUID userId) {
        long activeCount = borrowRecordRepository.countByKeycloakUserIdAndStatus(userId, BorrowStatus.ACTIVE);
        if (activeCount >= 3) {
            throw new IllegalStateException("Limit of 3 active books exceeded");
        }
        try {
            bookInventoryPort.decrementCopies(bookId, getCurrentToken());
        } catch (Exception e) {
            throw new IllegalStateException("Book is not available or service error");
        }

        BorrowRecord record = BorrowRecord.builder()
                .bookId(bookId)
                .keycloakUserId(userId)
                .borrowDate(LocalDateTime.now())
                .status(BorrowStatus.ACTIVE)
                .build();
        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        asyncLibraryService.sendBookBorrowedNotification(userId, "Book ID: " + bookId, record.getBorrowDate());

        return borrowRecordMapper.toDto(savedRecord);
    }

    @Transactional
    public BorrowRecordDto returnBook(Long recordId, UUID userId) {
        BorrowRecord record = borrowRecordRepository.findByIdAndKeycloakUserId(recordId, userId)
                .orElseThrow(() -> new EntityNotFoundException("Record not found or access denied"));

        if (record.getStatus() == BorrowStatus.RETURNED) {
            throw new IllegalStateException("Book is already returned");
        }
        try {
            bookInventoryPort.incrementCopies(record.getBookId(), getCurrentToken());
        } catch (Exception e) {
            throw new IllegalStateException("Could not return book to catalog");
        }

        record.setStatus(BorrowStatus.RETURNED);
        record.setReturnDate(LocalDateTime.now());
        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        return borrowRecordMapper.toDto(savedRecord);
    }

    @Transactional(readOnly = true)
    public List<BorrowRecordDto> getActiveRecordsByUser(UUID userId) {
        return borrowRecordRepository.findByKeycloakUserIdAndStatusIn(
                        userId, List.of(BorrowStatus.ACTIVE, BorrowStatus.PENDING))
                .stream()
                .map(borrowRecordMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processOverdueReminders(int daysOverdue, String messageType) {
        LocalDateTime thresholdDate = LocalDateTime.now().minusDays(daysOverdue);
        List<BorrowRecord> overdueRecords = borrowRecordRepository.findOverdueWithoutReminder(BorrowStatus.ACTIVE, thresholdDate);
        if (overdueRecords.isEmpty()) { return; }

        for (BorrowRecord record : overdueRecords) {
            asyncLibraryService.sendNotification(record.getId(), "user_" + record.getKeycloakUserId() + "@example.com");
            record.setReminderSentDate(LocalDateTime.now());
        }
        borrowRecordRepository.saveAll(overdueRecords);
    }

    @Transactional(readOnly = true)
    public void generateStatsReport(String reportName, boolean includeInactive) {
        log.info("Starting generation of report: {}", reportName);
        long activeRecordsCount = borrowRecordRepository.count();
        if (includeInactive) {
            log.info("Report '{}' generated with all records. Total: {}", reportName, activeRecordsCount);
        } else {
            long activeOnlyCount = borrowRecordRepository.findAll().stream()
                    .filter(r -> r.getStatus() == BorrowStatus.ACTIVE)
                    .count();
            log.info("Report '{}' generated with active records only. Total: {}", reportName, activeOnlyCount);
        }
    }
}