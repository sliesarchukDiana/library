package com.ascariaa.library.controller;

import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.domain.dto.BorrowRecordDto;
import com.ascariaa.library.domain.exception.ErrorResponse;
import jakarta.validation.Valid;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.List;

@Controller
@RequestMapping("/")
public class WebController {

    private final RestClient restClient;
    private final String BOOK_SERVICE_URL = "http://localhost:8081/api/books";
    private final String BORROW_SERVICE_URL = "http://localhost:8082/api/borrow";

    public WebController() {
        this.restClient = RestClient.create();
    }

    @GetMapping
    public String index(Model model,
                        @AuthenticationPrincipal OidcUser oidcUser,
                        @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authClient) {

        String token = getBearerToken(authClient);

        if (oidcUser != null) {
            model.addAttribute("username", oidcUser.getPreferredUsername());
            try {
                List<BorrowRecordDto> myBooks = restClient.get()
                        .uri(BORROW_SERVICE_URL + "/active")
                        .header(HttpHeaders.AUTHORIZATION, token)
                        .retrieve()
                        .body(new ParameterizedTypeReference<List<BorrowRecordDto>>() {});
                model.addAttribute("myBooks", myBooks);
            } catch (Exception e) {
                model.addAttribute("error", "Не вдалося завантажити ваші книги");
            }
        }

        try {
            List<BookDto> books = restClient.get()
                    .uri(BOOK_SERVICE_URL)
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<BookDto>>() {});
            model.addAttribute("books", books);
        } catch (Exception e) {
            model.addAttribute("error", "Не вдалося завантажити каталог книг");
        }

        return "index";
    }

    @PostMapping("/ui/borrow/{bookId}")
    public String borrowBook(@PathVariable Long bookId,
                             @AuthenticationPrincipal OidcUser oidcUser,
                             @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authClient,
                             Model model) {
        try {
            restClient.post()
                    .uri(BORROW_SERVICE_URL + "/" + bookId)
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(authClient))
                    .retrieve()
                    .toBodilessEntity();
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
            return index(model, oidcUser, authClient);
        }
    }

    @PostMapping("/ui/borrow/return/{recordId}")
    public String returnBook(@PathVariable Long recordId,
                             @AuthenticationPrincipal OidcUser oidcUser,
                             @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authClient,
                             Model model) {
        try {
            restClient.post()
                    .uri(BORROW_SERVICE_URL + "/return/" + recordId)
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(authClient))
                    .retrieve()
                    .toBodilessEntity();
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
            return index(model, oidcUser, authClient);
        }
    }

    @GetMapping("/ui/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new BookCreateDto());
        return "book-form";
    }

    @PostMapping("/books")
    public String createBook(@Valid @ModelAttribute("book") BookCreateDto bookCreateDto,
                             BindingResult bindingResult,
                             @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authClient) {
        if (bindingResult.hasErrors()) {
            return "book-form";
        }

        restClient.post()
                .uri(BOOK_SERVICE_URL)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken(authClient))
                .contentType(MediaType.APPLICATION_JSON)
                .body(bookCreateDto)
                .retrieve()
                .toBodilessEntity();

        return "redirect:/";
    }

    @PostMapping("/ui/books/delete/{id}")
    public String deleteBook(@PathVariable Long id,
                             @AuthenticationPrincipal OidcUser oidcUser,
                             @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authClient,
                             Model model) {
        try {
            restClient.delete()
                    .uri(BOOK_SERVICE_URL + "/" + id)
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(authClient))
                    .retrieve()
                    .toBodilessEntity();
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
            return index(model, oidcUser, authClient);
        }
    }

    @PostMapping("/ui/reports/generate")
    public String generateReport(@AuthenticationPrincipal OidcUser oidcUser,
                                 @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authClient,
                                 Model model) {
        try {
            restClient.post()
                    .uri(BORROW_SERVICE_URL + "/reports/generate")
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken(authClient))
                    .retrieve()
                    .toBodilessEntity();
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", extractErrorMessage(e));
            return index(model, oidcUser, authClient);
        }
    }

    private String getBearerToken(OAuth2AuthorizedClient authClient) {
        if (authClient != null) {
            return "Bearer " + authClient.getAccessToken().getTokenValue();
        }
        return "";
    }

    private String extractErrorMessage(Exception e) {
        if (e instanceof RestClientResponseException restException) {
            try {
                ErrorResponse errorResponse = restException.getResponseBodyAs(ErrorResponse.class);
                if (errorResponse != null && errorResponse.message() != null) {
                    return errorResponse.message();
                }
            } catch (Exception ignored) {
            }
            return restException.getMessage();
        }
        return e.getMessage();
    }
}