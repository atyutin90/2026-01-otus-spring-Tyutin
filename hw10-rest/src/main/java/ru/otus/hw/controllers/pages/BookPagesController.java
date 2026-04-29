package ru.otus.hw.controllers.pages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class BookPagesController {

    @GetMapping("/books")
    private String listPage() {
        return "/book/list";
    }

    @GetMapping("/books/new")
    private String newPage() {
        return "/book/show";
    }

    @GetMapping("/books/{id}")
    private String showPage(@PathVariable long id) {
        return "/book/show";
    }
}
