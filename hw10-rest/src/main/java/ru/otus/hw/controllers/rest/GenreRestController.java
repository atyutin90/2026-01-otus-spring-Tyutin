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
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequiredArgsConstructor
public class GenreRestController {

    private final GenreService genreService;

    @GetMapping("/api/genres")
    public List<GenreDto> getAll() {
        return genreService.findAll();
    }

    @GetMapping("/api/genres/{id}")
    public GenreDto get(@PathVariable long id) {
        return genreService.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/api/genres/{id}")
    public void delete(@PathVariable long id) {
        genreService.deleteById(id);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/genres")
    private GenreDto create(@Valid @RequestBody GenreDto genre) {
        return genreService.create(genre);
    }

    @PatchMapping("/api/genres/{id}")
    private GenreDto update(@PathVariable long id,
                            @Valid @RequestBody GenreDto genre) {
        return genreService.update(genre.withId(id));
    }
}
