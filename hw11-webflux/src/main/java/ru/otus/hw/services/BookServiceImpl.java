package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.exceptions.GenresException;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @Override
    public Flux<BookDto> findAll() {
        return bookRepository.findAll().map(BookConverter::bookDtoOf);
    }

    @Override
    public Mono<BookDto> findById(long id) {
        return bookRepository.findById(id)
            .switchIfEmpty(Mono.error(new BookNotFoundException("Book with id: %d not found".formatted(id))))
            .map(BookConverter::bookDtoOf);
    }

    @Override
    @Transactional
    public Mono<BookDto> create(BookDto data) {
        return save(0, data.title(), data.authorId(), data.genreIds())
            .map(BookConverter::bookDtoOf);
    }

    @Override
    @Transactional
    public Mono<BookDto> update(BookDto data) {
        var id = data.id();
        return bookRepository.findById(id)
            .switchIfEmpty(Mono.error(new BookNotFoundException("Book with id: %d not found".formatted(id))))
            .flatMap(book -> save(data.id(), data.title(), data.authorId(), data.genreIds()))
            .map(BookConverter::bookDtoOf);
    }

    @Override
    @Transactional
    public Mono<Void> deleteById(long id) {
        return bookRepository.deleteById(id);
    }

    private Mono<Book> save(long id, String title, long authorId, Set<Long> genresIds) {
        if (isEmpty(genresIds)) {
            return Mono.error(new GenresException("Genres ids must not be null"));
        }
        return authorRepository.findById(authorId)
            .switchIfEmpty(Mono.error(new AuthorNotFoundException("Author with id %d not found".formatted(authorId))))
            .flatMap(author ->
                genreRepository.findByIdIsIn(genresIds)
                    .collectList()
                    .flatMap(genres -> {
                        if (genres.isEmpty() || genres.size() != genresIds.size()) {
                            return Mono.error(
                                new GenreNotFoundException(
                                    "One or all genres with ids %s not found".formatted(genresIds)
                                )
                            );
                        }
                        var book = new Book(id, title, authorId, genres.stream().map(Genre::getId).toList());
                        return bookRepository.save(book);
                    })
            );
    }
}
