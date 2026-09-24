package com.ascariaa.library.repository;

import com.ascariaa.library.entity.BorrowRecord;
import com.ascariaa.library.entity.enums.BorrowStatus;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, Long> {

    long countByKeycloakUserIdAndStatus(UUID keycloakUserId, BorrowStatus status);

    Optional<BorrowRecord> findByIdAndKeycloakUserId(Long id, UUID keycloakUserId);

    List<BorrowRecord> findByKeycloakUserIdAndStatus(UUID keycloakUserId, BorrowStatus status);

    boolean existsByBookIdAndStatus(Long bookId, BorrowStatus status);

    void deleteByBookId(Long bookId);
}