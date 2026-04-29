package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public Flux<GenreDto> findAll() {
        return genreRepository.findAll().map(GenreConverter::genreDtoOf);
    }

    @Override
    public Mono<GenreDto> findById(long id) {
        return genreRepository.findById(id)
            .map(GenreConverter::genreDtoOf)
            .switchIfEmpty(Mono.error(new GenreNotFoundException("Genre with id: %d not found".formatted(id))));
    }

    @Override
    public Mono<GenreDto> update(GenreDto data) {
        var id = data.id();
        return genreRepository.findById(id)
            .switchIfEmpty(Mono.error(new GenreNotFoundException("Genre with id: %d not found".formatted(id))))
            .map(g -> {
                g.setName(data.name());
                return g;
            }).flatMap(genreRepository::save)
            .map(GenreConverter::genreDtoOf);
    }

    @Override
    public Mono<GenreDto> create(GenreDto data) {
        return genreRepository.save(new Genre(0L, data.name()))
            .map(GenreConverter::genreDtoOf);
    }

    @Override
    public Mono<Void> deleteById(long id) {
        return genreRepository.deleteById(id);
    }
}
