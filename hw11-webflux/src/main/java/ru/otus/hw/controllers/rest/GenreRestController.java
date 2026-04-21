package ru.otus.hw.controllers.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@Deprecated(since = "Используются Functional Endpoint: ru/otus/hw/routers/rest/GenreRouter.java")
@RestController
@RequiredArgsConstructor
public class GenreRestController {

  /*  private final GenreService genreService;

    @GetMapping("/api/genres")
    public Flux<GenreDto> getAll() {
        return genreService.findAll();
    }

    @GetMapping("/api/genres/{id}")
    public Mono<GenreDto> get(@PathVariable long id) {
        return genreService.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/api/genres/{id}")
    public Mono<Void> delete(@PathVariable long id) {
        return genreService.deleteById(id);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/genres")
    private Mono<GenreDto> create(@Valid @RequestBody GenreDto genre) {
        return genreService.create(genre);
    }

    @PatchMapping("/api/genres/{id}")
    private Mono<GenreDto> update(@PathVariable long id,
                            @Valid @RequestBody GenreDto genre) {
        return genreService.update(genre.withId(id));
    }*/
}
