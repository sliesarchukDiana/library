package com.ascariaa.library.controller;

import com.ascariaa.library.dto.BookCreateDto;
import com.ascariaa.library.service.BookService;
import com.ascariaa.library.service.BorrowRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class WebController {

    private final BookService bookService;
    private final BorrowRecordService borrowRecordService;

    @GetMapping
    public String index(Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        if (oidcUser != null) {
            UUID userId = UUID.fromString(Objects.requireNonNull(oidcUser.getSubject()));
            model.addAttribute("username", oidcUser.getPreferredUsername());
            model.addAttribute("myBooks", borrowRecordService.getActiveRecordsByUser(userId));
        }
        model.addAttribute("books", bookService.getAllBooks());
        return "index";
    }

    @PostMapping("/ui/borrow/{bookId}")
    public String borrowBook(@PathVariable Long bookId, @AuthenticationPrincipal OidcUser oidcUser, Model model) {
        try {
            borrowRecordService.borrowBook(bookId, UUID.fromString(Objects.requireNonNull(oidcUser.getSubject())));
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return index(model, oidcUser);
        }
    }

    @PostMapping("/ui/borrow/return/{recordId}")
    public String returnBook(@PathVariable Long recordId, @AuthenticationPrincipal OidcUser oidcUser, Model model) {
        try {
            borrowRecordService.returnBook(recordId, UUID.fromString(Objects.requireNonNull(oidcUser.getSubject())));
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return index(model, oidcUser);
        }
    }

    @GetMapping("/ui/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new BookCreateDto());
        return "book-form";
    }

    @PostMapping("/ui/books")
    public String createBook(@ModelAttribute("book") @Valid BookCreateDto bookCreateDto) {
        bookService.createBook(bookCreateDto);
        return "redirect:/";
    }

    @PostMapping("/ui/books/delete/{id}")
    public String deleteBook(@PathVariable Long id, Model model, @AuthenticationPrincipal OidcUser oidcUser) {
        try {
            bookService.deleteBook(id);
            return "redirect:/";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return index(model, oidcUser);
        }
    }
}