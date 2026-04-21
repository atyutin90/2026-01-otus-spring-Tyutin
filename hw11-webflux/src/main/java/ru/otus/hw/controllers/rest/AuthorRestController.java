package ru.otus.hw.controllers.rest;

@Deprecated(since = "Используются Functional Endpoint: ru/otus/hw/routers/rest/AuthorRouter.java")
//@RestController
//@RequiredArgsConstructor
public class AuthorRestController {

/*    private final AuthorService authorService;

    @GetMapping("/api/authors")
    public Flux<AuthorDto> getAll() {
        return authorService.findAll();
    }

    @GetMapping("/api/authors/{id}")
    public Mono<AuthorDto> get(@PathVariable long id) {
        return authorService.findById(id);
    }

    @ResponseStatus(NO_CONTENT)
    @DeleteMapping("/api/authors/{id}")
    public Mono<Void> delete(@PathVariable long id) {
        return authorService.deleteById(id);
    }

    @ResponseStatus(CREATED)
    @PostMapping("/api/authors")
    private Mono<AuthorDto> create(@Valid @RequestBody AuthorDto author) {
        return authorService.create(author);
    }

    @PatchMapping("/api/authors/{id}")
    private Mono<AuthorDto> update(@PathVariable long id,
                             @Valid @RequestBody AuthorDto author) {
        return authorService.update(author.withId(id));
    }*/
}
