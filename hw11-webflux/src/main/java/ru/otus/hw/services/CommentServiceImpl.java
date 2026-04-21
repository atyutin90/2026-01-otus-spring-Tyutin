package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.exceptions.CommentNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.repositories.CommentRepository;
import ru.otus.hw.repositories.BookRepository;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final BookRepository bookRepository;

    @Override
    public Flux<CommentDto> findAll() {
        return commentRepository.findAll()
            .map(CommentConverter::commentDtoOf);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CommentDto> findByBookId(long bookId) {
        return bookRepository.findById(bookId)
            .switchIfEmpty(Mono.error(new BookNotFoundException("Book with id %d not found".formatted(bookId))))
            .flatMapMany(book -> commentRepository.findByBookId(book.getId()))
            .map(CommentConverter::commentDtoOf);
    }

    @Override
    public Mono<CommentDto> findById(long id) {
        return commentRepository.findById(id)
            .switchIfEmpty(Mono.error(new CommentNotFoundException("Comment with id: %d not found".formatted(id))))
            .map(CommentConverter::commentDtoOf);
    }

    @Override
    @Transactional
    public Mono<CommentDto> update(CommentDto data) {
        var id = data.id();
        return commentRepository.findById(id)
            .switchIfEmpty(Mono.error(new CommentNotFoundException("Comment with id: %d not found".formatted(id))))
            .map(comment -> {
                comment.setText(data.text());
                return comment;
            })
            .flatMap(commentRepository::save)
            .map(CommentConverter::commentDtoOf);
    }

    @Override
    @Transactional
    public Mono<CommentDto> create(CommentDto data) {
        return save(0L, data.text(), data.bookId()).map(CommentConverter::commentDtoOf);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return commentRepository.deleteById(id);
    }

    private Mono<Comment> save(long id, String text, long bookId) {
        return bookRepository.findById(bookId)
            .switchIfEmpty(Mono.error(new BookNotFoundException("Book with id %d not found".formatted(bookId))))
            .map(it -> new Comment(id, text, it.getId()))
            .flatMap(commentRepository::save);
    }
}
