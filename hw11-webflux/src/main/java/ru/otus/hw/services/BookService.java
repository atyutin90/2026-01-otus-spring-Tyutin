package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.BookDto;

public interface BookService {

    Flux<BookDto> findAll();

    Mono<BookDto> findById(long id);

    Mono<BookDto> update(BookDto data);

    Mono<BookDto> create(BookDto data);

    Mono<Void> deleteById(long id);
}
