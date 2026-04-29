package ru.otus.hw.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

@Controller
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/books/{bookId}/comments")
    private String editPage(@PathVariable long bookId, Model model) {
        model.addAttribute("bookId", bookId);
        model.addAttribute("comment", new CommentDto(null, null));
        return "/comment/edit";
    }

    @PostMapping("/books/{bookId}/comments")
    private String savePage(@PathVariable long bookId,
                       @Valid @ModelAttribute("comment") CommentDto comment,
                       BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "/comment/edit";
        }

        if (comment.id() != null) {
            commentService.update(comment);
        } else {
            commentService.create(bookId, comment);
        }
        return "redirect:/books/%d".formatted(bookId);
    }

    @GetMapping("/books/{bookId}/comments/{id}")
    private String editPage(@PathVariable long bookId,
                        @PathVariable long id,
                        Model model) {
        var comment = commentService.findById(id);
        model.addAttribute("bookId", bookId);
        model.addAttribute("comment", comment);
        return "/comment/edit";
    }

    @DeleteMapping("/books/{bookId}/comments/{id}")
    private String deleteComment(@PathVariable long bookId,
                          @PathVariable long id) {
        commentService.deleteById(id);
        return "redirect:/books/%d".formatted(bookId);
    }
}
