package com.ascariaa.library.controller;

import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.grpc.*;
import com.ascariaa.library.service.BookService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@Slf4j
@GrpcService
@ConditionalOnProperty(name = "app.grpc.server.enabled", havingValue = "true")
@RequiredArgsConstructor
public class BookGrpcEndpoint extends BookGrpcServiceGrpc.BookGrpcServiceImplBase {

    private final BookService bookService;

    @Override
    public void getAllBooks(Empty request, StreamObserver<BookListResponse> responseObserver) {
        try {
            var books = bookService.getAllBooks().stream()
                    .map(this::mapToProto)
                    .toList();
            responseObserver.onNext(BookListResponse.newBuilder().addAllBooks(books).build());
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void createBook(CreateBookRequest request, StreamObserver<BookResponse> responseObserver) {
        try {
            BookCreateDto dto = new BookCreateDto();
            dto.setTitle(request.getTitle());
            dto.setAuthor(request.getAuthor());
            dto.setAvailableCopies(request.getAvailableCopies());

            BookDto created = bookService.createBook(dto);
            responseObserver.onNext(mapToProto(created));
            responseObserver.onCompleted();
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    @Override
    public void deleteBook(DeleteBookRequest request, StreamObserver<Empty> responseObserver) {
        try {
            bookService.deleteBook(request.getId());
            responseObserver.onNext(Empty.newBuilder().build());
            responseObserver.onCompleted();
        } catch (EntityNotFoundException e) {
            responseObserver.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());
        } catch (Exception e) {
            responseObserver.onError(Status.INTERNAL.withDescription(e.getMessage()).asRuntimeException());
        }
    }

    private BookResponse mapToProto(BookDto dto) {
        return BookResponse.newBuilder()
                .setId(dto.getId())
                .setTitle(dto.getTitle())
                .setAuthor(dto.getAuthor())
                .setAvailableCopies(dto.getAvailableCopies())
                .build();
    }
}