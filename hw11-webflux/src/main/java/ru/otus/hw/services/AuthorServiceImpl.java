package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    public Flux<AuthorDto> findAll() {
        return authorRepository.findAll().map(AuthorConverter::authorDtoOf);
    }

    @Override
    public Mono<AuthorDto> findById(long id) {
        return authorRepository.findById(id)
            .map(AuthorConverter::authorDtoOf)
            .switchIfEmpty(Mono.error(new AuthorNotFoundException("Author with id: %d not found".formatted(id))));
    }

    @Transactional
    @Override
    public Mono<AuthorDto> update(AuthorDto data) {
        var id = data.id();
        return authorRepository.findById(id)
            .switchIfEmpty(Mono.error(new AuthorNotFoundException("Author with id: %d not found".formatted(id))))
            .map(a -> {
                a.setFullName(data.fullName());
                return a;
            }).flatMap(authorRepository::save)
            .map(AuthorConverter::authorDtoOf);
    }

    @Transactional
    @Override
    public Mono<AuthorDto> create(AuthorDto data) {
        return authorRepository.save(new Author(0L, data.fullName()))
            .map(AuthorConverter::authorDtoOf);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(long id) {
        return authorRepository.deleteById(id);
    }
}
