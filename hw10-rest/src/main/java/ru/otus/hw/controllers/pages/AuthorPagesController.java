package ru.otus.hw.controllers.pages;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class AuthorPagesController {

    @GetMapping("/authors")
    private String listPage() {
        return "/author/list";
    }

    @GetMapping("/authors/new")
    private String newPage() {
        return "/author/show";
    }

    @GetMapping("/authors/{id}")
    private String showPage(@PathVariable long id) {
        return "/author/show";
    }
}
