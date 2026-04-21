package ru.otus.hw.services;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.dto.CommentDto;

public interface CommentService {

    Flux<CommentDto> findAll();

    Flux<CommentDto> findByBookId(long bookId);

    Mono<CommentDto> findById(long id);

    Mono<CommentDto> update(CommentDto data);

    Mono<CommentDto> create(CommentDto data);

    Mono<Void> deleteById(long id);
}
