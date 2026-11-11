package com.ascariaa.library.adapter;

import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.domain.exception.ServiceUnavailableException;
import com.ascariaa.library.grpc.*;
import com.ascariaa.library.port.BookCatalogPort;
import io.grpc.StatusRuntimeException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class GrpcBookCatalogAdapter implements BookCatalogPort {

    @GrpcClient("book-service")
    private BookGrpcServiceGrpc.BookGrpcServiceBlockingStub bookGrpcStub;

    private final long grpcDeadlineMs;

    @Override
    public List<BookDto> getAllBooks(String token) {
        try {
            BookListResponse response = getStubWithDeadline().getAllBooks(Empty.newBuilder().build());
            return response.getBooksList().stream().map(this::mapToDto).toList();
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e);
        }
    }

    @Override
    public void createBook(BookCreateDto dto, String token) {
        try {
            CreateBookRequest request = CreateBookRequest.newBuilder()
                    .setTitle(dto.getTitle())
                    .setAuthor(dto.getAuthor())
                    .setAvailableCopies(dto.getAvailableCopies())
                    .build();
            getStubWithDeadline().createBook(request);
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e);
        }
    }

    @Override
    public void deleteBook(Long id, String token) {
        try {
            getStubWithDeadline().deleteBook(DeleteBookRequest.newBuilder().setId(id).build());
        } catch (StatusRuntimeException e) {
            throw handleGrpcException(e);
        }
    }

    private BookGrpcServiceGrpc.BookGrpcServiceBlockingStub getStubWithDeadline() {
        return bookGrpcStub.withDeadlineAfter(grpcDeadlineMs, TimeUnit.MILLISECONDS);
    }

    private RuntimeException handleGrpcException(StatusRuntimeException e) {
        log.error("gRPC call failed with status: {}", e.getStatus().getCode());
        return switch (e.getStatus().getCode()) {
            case NOT_FOUND -> new EntityNotFoundException(e.getStatus().getDescription());
            case UNAVAILABLE, DEADLINE_EXCEEDED -> new ServiceUnavailableException("Book service is unavailable via gRPC", e);
            default -> new ServiceUnavailableException("Unexpected gRPC error: " + e.getStatus().getCode(), e);
        };
    }

    private BookDto mapToDto(BookResponse proto) {
        BookDto dto = new BookDto();
        dto.setId(proto.getId());
        dto.setTitle(proto.getTitle());
        dto.setAuthor(proto.getAuthor());
        dto.setAvailableCopies(proto.getAvailableCopies());
        return dto;
    }
}