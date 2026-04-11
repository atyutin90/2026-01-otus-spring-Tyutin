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
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.services.BookService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
public class BookRestController {

    private final BookService bookService;

    @GetMapping("/api/books")
    public List<BookDto> getAll() {
        return bookService.findAll();
    }

    @GetMapping("/api/books/{id}")
    public BookDto get(@PathVariable long id) {
        return bookService.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/api/books/{id}")
    public void delete(@PathVariable long id) {
        bookService.deleteById(id);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/books")
    private BookDto create(@Valid @RequestBody BookDto author) {
        return bookService.create(author);
    }

    @PatchMapping("/api/books/{id}")
    private BookDto update(@PathVariable long id,
                           @Valid @RequestBody BookDto bookDto) {
        return bookService.update(bookDto.withId(id));
    }
}
