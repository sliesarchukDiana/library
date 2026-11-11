package com.ascariaa.library.port;

public interface BookInventoryPort {
    void decrementCopies(Long bookId, String token);
    void incrementCopies(Long bookId, String token);
}