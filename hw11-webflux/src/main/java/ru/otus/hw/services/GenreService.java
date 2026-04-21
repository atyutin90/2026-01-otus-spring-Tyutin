package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.GenreDto;

public interface GenreService {
    Flux<GenreDto> findAll();

    Mono<GenreDto> findById(long id);

    Mono<GenreDto> update(GenreDto data);

    Mono<GenreDto> create(GenreDto data);

    Mono<Void> deleteById(long id);
}
