package ru.otus.hw.controllers.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
public class CommentRestController {

    private final CommentService commentService;

    @GetMapping("/api/books/{bookId}/comments")
    private List<CommentDto> getAll(@PathVariable long bookId) {
        return commentService.findByBookId(bookId);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/books/{bookId}/comments")
    private CommentDto create(@PathVariable long bookId, @Valid @RequestBody CommentDto comment) {
        return commentService.create(comment.withBookId(bookId));
    }

    @GetMapping("/api/comments/{id}")
    private CommentDto get(@PathVariable long id) {
        return commentService.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/api/comments/{id}")
    private void delete(@PathVariable long id) {
        commentService.deleteById(id);
    }

    @PatchMapping("/api/comments/{id}")
    private CommentDto update(@PathVariable long id,
                              @Valid @RequestBody CommentDto comment) {
        return commentService.update(comment.withId(id));
    }
}
