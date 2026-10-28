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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        mockServer.expect(requestTo("http://localhost:8081/api/books/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess());

        String view = webController.deleteBook(1L, oidcUser, null, model);

        assertEquals("redirect:/", view);
        mockServer.verify();
    }

    @Test
    void returnBook_success_redirects() {
        mockServer.expect(requestTo("http://localhost:8082/api/borrow/return/1"))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess());

        String view = webController.returnBook(1L, oidcUser, null, model);

        assertEquals("redirect:/", view);
        mockServer.verify();
    }
}
