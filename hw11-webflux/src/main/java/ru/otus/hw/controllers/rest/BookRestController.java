package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.RestController;

@Deprecated(since = "Используются Functional Endpoint: ru/otus/hw/routers/rest/BookRouter.java")
@RestController
@RequiredArgsConstructor
public class BookRestController {

/*    private final BookService bookService;

    @GetMapping("/api/books")
    public Flux<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/api/books/{id}")
    public Mono<BookDto> get(@PathVariable long id) {
        return bookService.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/api/books/{id}")
    public Mono<Void> delete(@PathVariable long id) {
        return bookService.deleteById(id);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/books")
    private Mono<BookDto> create(@Valid @RequestBody BookDto author) {
        return bookService.create(author);
    }

    @PatchMapping("/api/books/{id}")
    private Mono<BookDto> update(@PathVariable long id,
                           @Valid @RequestBody BookDto bookDto) {
        return bookService.update(bookDto.withId(id));
    }*/
}
