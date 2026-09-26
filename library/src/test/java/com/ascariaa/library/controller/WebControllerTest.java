package com.ascariaa.library.controller;

import com.ascariaa.library.dto.BookCreateDto;
import com.ascariaa.library.service.BookService;
import com.ascariaa.library.service.BorrowRecordService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebControllerTest {

    @Mock
    private BookService bookService;

    @Mock
    private BorrowRecordService borrowRecordService;

    @Mock
    private Model model;

    @Mock
    private OidcUser oidcUser;

    @InjectMocks
    private WebController webController;

    @Test
    void index_authenticatedUser_addsAttributesAndReturnsIndex() {
        UUID userId = UUID.randomUUID();
        when(oidcUser.getSubject()).thenReturn(userId.toString());
        when(oidcUser.getPreferredUsername()).thenReturn("testuser");

        String view = webController.index(model, oidcUser);

        assertEquals("index", view);
        verify(model).addAttribute("username", "testuser");
        verify(model).addAttribute(eq("myBooks"), any());
        verify(model).addAttribute(eq("books"), any());
    }

    @Test
    void index_unauthenticatedUser_addsBooksAndReturnsIndex() {
        String view = webController.index(model, null);

        assertEquals("index", view);
        verify(model).addAttribute(eq("books"), any());
        verify(model, never()).addAttribute(eq("username"), any());
    }

    @Test
    void borrowBook_success_redirects() {
        UUID userId = UUID.randomUUID();
        when(oidcUser.getSubject()).thenReturn(userId.toString());

        String view = webController.borrowBook(1L, oidcUser, model);

        assertEquals("redirect:/", view);
        verify(borrowRecordService).borrowBook(1L, userId);
    }

    @Test
    void borrowBook_exception_addsErrorAndReturnsIndex() {
        UUID userId = UUID.randomUUID();
        when(oidcUser.getSubject()).thenReturn(userId.toString());
        doThrow(new IllegalStateException("Limit")).when(borrowRecordService).borrowBook(1L, userId);

        String view = webController.borrowBook(1L, oidcUser, model);

        assertEquals("index", view);
        verify(model).addAttribute("error", "Limit");
    }

    @Test
    void newBookForm_returnsFormView() {
        String view = webController.newBookForm(model);

        assertEquals("book-form", view);
        verify(model).addAttribute(eq("book"), any(BookCreateDto.class));
    }

    @Test
    void createBook_redirects() {
        BookCreateDto dto = new BookCreateDto();
        String view = webController.createBook(dto);

        assertEquals("redirect:/", view);
        verify(bookService).createBook(dto);
    }

    @Test
    void deleteBook_success_redirects() {
        String view = webController.deleteBook(1L, model, oidcUser);

        assertEquals("redirect:/", view);
        verify(bookService).deleteBook(1L);
    }

    @Test
    void deleteBook_exception_addsErrorAndReturnsIndex() {
        when(oidcUser.getSubject()).thenReturn(UUID.randomUUID().toString());
        doThrow(new IllegalStateException("Cannot delete")).when(bookService).deleteBook(1L);

        String view = webController.deleteBook(1L, model, oidcUser);

        assertEquals("index", view);
        verify(model).addAttribute("error", "Cannot delete");
    }
}