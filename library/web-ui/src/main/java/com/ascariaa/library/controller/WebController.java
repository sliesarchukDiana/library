package com.ascariaa.library.controller;

import com.ascariaa.library.domain.dto.BookCreateDto;
import com.ascariaa.library.domain.dto.BookDto;
import com.ascariaa.library.domain.dto.BorrowRecordDto;
import com.ascariaa.library.port.BookCatalogPort;
import com.ascariaa.library.port.BorrowRecordPort;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.RegisteredOAuth2AuthorizedClient;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
public class WebController {

    private final BookCatalogPort bookCatalogPort;
    private final BorrowRecordPort borrowRecordPort;

    public WebController(BookCatalogPort bookCatalogPort, BorrowRecordPort borrowRecordPort) {
        this.bookCatalogPort = bookCatalogPort;
        this.borrowRecordPort = borrowRecordPort;
    }

    @GetMapping
    public String index(Model model,
                        @AuthenticationPrincipal OidcUser oidcUser,
                        @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authClient) {
        String token = getBearerToken(authClient);
        List<BorrowRecordDto> myBooks = null;

        if (oidcUser != null) {
            model.addAttribute("username", oidcUser.getPreferredUsername());
            try {
                myBooks = borrowRecordPort.getActiveRecords(token);
            } catch (Exception e) {
                model.addAttribute("error", e.getMessage());
            }
        }

        try {
            List<BookDto> books = bookCatalogPort.getAllBooks(token);
            model.addAttribute("books", books);

            if (myBooks != null) {
                myBooks.forEach(record -> {
                    books.stream()
                            .filter(b -> b.getId().equals(record.getBookId()))
                            .findFirst()
                            .ifPresent(b -> record.setBookTitle(b.getTitle()));
                });
                model.addAttribute("myBooks", myBooks);
            }
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "index";
    }

    @PostMapping("/ui/borrow/{bookId}")
    public String borrowBook(@PathVariable Long bookId,
                             @AuthenticationPrincipal OidcUser oidcUser,
                             @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authClient,
                             Model model) {
        try {
            borrowRecordPort.borrowBook(bookId, getBearerToken(authClient));
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return index(model, oidcUser, authClient);
        }
    }

    @PostMapping("/ui/borrow/return/{recordId}")
    public String returnBook(@PathVariable Long recordId,
                             @AuthenticationPrincipal OidcUser oidcUser,
                             @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authClient,
                             Model model) {
        try {
            borrowRecordPort.returnBook(recordId, getBearerToken(authClient));
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
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
        try {
            bookCatalogPort.createBook(bookCreateDto, getBearerToken(authClient));
        } catch (Exception e) {
            log.info("Something went wrong while creating a book: {}", e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/ui/books/delete/{id}")
    public String deleteBook(@PathVariable Long id,
                             @AuthenticationPrincipal OidcUser oidcUser,
                             @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authClient,
                             Model model) {
        try {
            bookCatalogPort.deleteBook(id, getBearerToken(authClient));
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return index(model, oidcUser, authClient);
        }
    }

    @PostMapping("/ui/reports/generate")
    public String generateReport(@AuthenticationPrincipal OidcUser oidcUser,
                                 @RegisteredOAuth2AuthorizedClient("keycloak") OAuth2AuthorizedClient authClient,
                                 Model model) {
        try {
            borrowRecordPort.generateReport(getBearerToken(authClient));
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return index(model, oidcUser, authClient);
        }
    }

    private String getBearerToken(OAuth2AuthorizedClient authClient) {
        if (authClient != null) {
            return "Bearer " + authClient.getAccessToken().getTokenValue();
        }
        return "";
    }
}