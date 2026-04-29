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
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
public class AuthorRestController {

    private final AuthorService authorService;

    @GetMapping("/api/authors")
    public List<AuthorDto> getAll() {
        return authorService.findAll();
    }

    @GetMapping("/api/authors/{id}")
    public AuthorDto get(@PathVariable long id) {
        return authorService.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/api/authors/{id}")
    public void delete(@PathVariable long id) {
        authorService.deleteById(id);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/authors")
    private AuthorDto create(@Valid @RequestBody AuthorDto author) {
        return authorService.create(author);
    }

    @PatchMapping("/api/authors/{id}")
    private AuthorDto update(@PathVariable long id,
                             @Valid @RequestBody AuthorDto author) {
        return authorService.update(author.withId(id));
    }
}
