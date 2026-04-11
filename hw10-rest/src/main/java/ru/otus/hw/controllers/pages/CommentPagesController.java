package ru.otus.hw.controllers.pages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class CommentPagesController {

    @GetMapping("/books/{bookId}/comments")
    private String editPage(@PathVariable long bookId) {
        return "/comment/show";
    }

    @GetMapping("/books/{bookId}/comments/{id}")
    private String editPage(@PathVariable long bookId,
                            @PathVariable long id) {
        return "/comment/show";
    }
}
