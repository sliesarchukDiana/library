package com.ascariaa.library.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.ui.Model;
import org.springframework.web.client.RestClient;
import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.port.BookCatalogPort;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.validation.BindingResult;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class WebControllerTest {

    @Mock
    private Model model;

    @Mock
    private OidcUser oidcUser;

    @Mock
    private BookCatalogPort bookCatalogPort;

    @InjectMocks
    private WebController webController;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder();
        mockServer = MockRestServiceServer.bindTo(builder).build();
        RestClient testClient = builder.build();
        ReflectionTestUtils.setField(webController, "restClient", testClient);
    }

    @Test
    void newBookForm_returnsFormView() {
        String view = webController.newBookForm(model);
        assertEquals("book-form", view);
        verify(model).addAttribute(eq("book"), any());
    }

    @Test
    void deleteBook_success_redirects() {
        String view = webController.deleteBook(1L, oidcUser, null, model);
        assertEquals("redirect:/", view);
        verify(bookCatalogPort).deleteBook(eq(1L), anyString());
    }

    @Test
    void returnBook_success_redirects() {
        mockServer.expect(requestTo("http://borrow-service:8082/api/borrow/return/1"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        String view = webController.returnBook(1L, oidcUser, null, model);

        assertEquals("redirect:/", view);
        mockServer.verify();
    }

    @Test
    void index_returnsViewAndAddsAttributes() {
        OAuth2AuthorizedClient authClient = mock(OAuth2AuthorizedClient.class);
        OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
        when(authClient.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getTokenValue()).thenReturn("token");
        when(oidcUser.getPreferredUsername()).thenReturn("testuser");

        mockServer.expect(requestTo("http://borrow-service:8082/api/borrow/active"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[]", MediaType.APPLICATION_JSON));

        when(bookCatalogPort.getAllBooks("Bearer token")).thenReturn(List.of());

        String view = webController.index(model, oidcUser, authClient);

        assertEquals("index", view);
        verify(model).addAttribute("username", "testuser");
        verify(model).addAttribute(eq("myBooks"), any());
        verify(model).addAttribute(eq("books"), any());
        verify(bookCatalogPort).getAllBooks("Bearer token");
    }

    @Test
    void borrowBook_success_redirects() {
        OAuth2AuthorizedClient authClient = mock(OAuth2AuthorizedClient.class);
        OAuth2AccessToken accessToken = mock(OAuth2AccessToken.class);
        when(authClient.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getTokenValue()).thenReturn("token");

        mockServer.expect(requestTo("http://borrow-service:8082/api/borrow/1"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        String view = webController.borrowBook(1L, oidcUser, authClient, model);

        assertEquals("redirect:/", view);
        mockServer.verify();
    }

    @Test
    void createBook_withErrors_returnsForm() {
        BindingResult bindingResult = mock(BindingResult.class);
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = webController.createBook(new BookCreateDto(), bindingResult, null);

        assertEquals("book-form", view);
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

        mockServer.expect(requestTo("http://borrow-service:8082/api/borrow/reports/generate"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        String view = webController.generateReport(oidcUser, authClient, model);

        assertEquals("redirect:/", view);
        mockServer.verify();
    }
}