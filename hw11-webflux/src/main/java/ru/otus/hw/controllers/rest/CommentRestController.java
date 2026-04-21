package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@Deprecated(since = "Используются Functional Endpoint: ru/otus/hw/routers/rest/CommentRouter.java")
@RestController
@RequiredArgsConstructor
public class CommentRestController {

   /* private final CommentService commentService;

    @GetMapping("/api/books/{bookId}/comments")
    private Flux<CommentDto> getAll(@PathVariable long bookId) {
        return commentService.findByBookId(bookId);
    }

    @GetMapping("/api/comments/{id}")
    private Mono<CommentDto> get(@PathVariable long id) {
        return commentService.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/api/comments/{id}")
    private Mono<Void> deleteComment(@PathVariable long id) {
        return commentService.deleteById(id);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/books/{bookId}/comments")
    private Mono<CommentDto> create(@PathVariable long bookId,
                                    @Valid @RequestBody CommentDto comment) {
        return commentService.create(comment.withBookId(bookId));
    }

    @PatchMapping("/api/comments/{id}")
    private Mono<CommentDto> update(@PathVariable long id,
                                    @Valid @RequestBody CommentDto comment) {
        return commentService.update(comment.withId(id));
    }*/
}
