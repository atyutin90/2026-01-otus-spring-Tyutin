package ru.otus.hw.controllers.pages;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GenrePagesController {

    @GetMapping("/genres")
    private String listPage() {
        return "/genre/list";
    }

    @GetMapping("/genres/new")
    private String newPage() {
        return "/genre/show";
    }

    @GetMapping("/genres/{id}")
    private String showPage(@PathVariable Long id) {
        return "/genre/show";
    }
}
