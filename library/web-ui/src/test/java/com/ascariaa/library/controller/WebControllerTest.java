package com.ascariaa.library.controller;

import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.domain.dto.BorrowRecordDto;
import com.ascariaa.library.port.BookCatalogPort;
import com.ascariaa.library.port.BorrowRecordPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebControllerTest {

    @Mock
    private Model model;

    @Mock
    private OidcUser oidcUser;

    @Mock
    private BookCatalogPort bookCatalogPort;

    @Mock
    private BorrowRecordPort borrowRecordPort;

    @InjectMocks
    private WebController webController;

    @Test
    void newBookForm_returnsFormView() {
        String view = webController.newBookForm(model);
        assertEquals("book-form", view);
        verify(model).addAttribute(eq("book"), any(BookCreateDto.class));
    }

    @Test
    void deleteBook_success_redirects() {
        String view = webController.deleteBook(1L, oidcUser, null, model);
        assertEquals("redirect:/", view);
        verify(bookCatalogPort).deleteBook(1L, "");
    }

    @Test
    void returnBook_success_redirects() {
        String view = webController.returnBook(1L, oidcUser, null, model);
        assertEquals("redirect:/", view);
        verify(borrowRecordPort).returnBook(1L, "");
    }

    @Test
    void index_returnsViewAndAddsAttributes() {
        OAuth2AuthorizedClient authClient = mock(OAuth2AuthorizedClient.class);
        OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
        when(authClient.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getTokenValue()).thenReturn("token");
        when(oidcUser.getPreferredUsername()).thenReturn("testuser");

        BorrowRecordDto mockRecord = new BorrowRecordDto();
        mockRecord.setId(1L);
        mockRecord.setBookId(10L);

        when(borrowRecordPort.getActiveRecords("Bearer token")).thenReturn(List.of(mockRecord));

        BookDto mockBook = new BookDto();
        mockBook.setId(10L);
        mockBook.setTitle("The Great Gatsby");
        when(bookCatalogPort.getAllBooks("Bearer token")).thenReturn(List.of(mockBook));

        String view = webController.index(model, oidcUser, authClient);

        assertEquals("index", view);
        verify(model).addAttribute("username", "testuser");
        verify(model).addAttribute(eq("myBooks"), any());
        verify(model).addAttribute(eq("books"), any());
        verify(bookCatalogPort).getAllBooks("Bearer token");
        verify(borrowRecordPort).getActiveRecords("Bearer token");
    }

    @Test
    void borrowBook_success_redirects() {
        OAuth2AuthorizedClient authClient = mock(OAuth2AuthorizedClient.class);
        OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
        when(authClient.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getTokenValue()).thenReturn("token");

        String view = webController.borrowBook(1L, oidcUser, authClient, model);

        assertEquals("redirect:/", view);
        verify(borrowRecordPort).borrowBook(1L, "Bearer token");
    }

    @Test
    void createBook_withErrors_returnsForm() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = webController.createBook(new BookCreateDto(), bindingResult, null);

        assertEquals("book-form", view);
        verifyNoInteractions(bookCatalogPort);
    }

    @Test
    void createBook_success_redirects() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(false);

        OAuth2AuthorizedClient authClient = mock(OAuth2AuthorizedClient.class);
        OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
        when(authClient.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getTokenValue()).thenReturn("token");

        BookCreateDto dto = new BookCreateDto();
        String view = webController.createBook(dto, bindingResult, authClient);

        assertEquals("redirect:/", view);
        verify(bookCatalogPort).createBook(dto, "Bearer token");
    }

    @Test
    void generateReport_success_redirects() {
        OAuth2AuthorizedClient authClient = mock(OAuth2AuthorizedClient.class);
        OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
        when(authClient.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getTokenValue()).thenReturn("token");

        String view = webController.generateReport(oidcUser, authClient, model);

        assertEquals("redirect:/", view);
        verify(borrowRecordPort).generateReport("Bearer token");
    }
}