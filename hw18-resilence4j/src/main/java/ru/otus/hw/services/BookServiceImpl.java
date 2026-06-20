package ru.otus.hw.services;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.exceptions.GenresException;
import ru.otus.hw.models.Book;
import ru.otus.hw.repositories.AuthorRepository;
import ru.otus.hw.repositories.BookRepository;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookServiceImpl implements BookService {

    private final AuthorRepository authorRepository;

    private final GenreRepository genreRepository;

    private final BookRepository bookRepository;

    @CircuitBreaker(name = "bookServiceBreaker")
    @Override
    public List<BookDto> findAll() {
        return bookRepository.findAll().stream()
                .map(BookConverter::bookDtoOf)
                .toList();
    }

    @CircuitBreaker(name = "bookServiceBreaker")
    @Override
    public BookDto findById(long id) {
        return bookRepository.findById(id)
                .map(BookConverter::bookDtoOf)
                .orElseThrow(() -> new BookNotFoundException("Book with id: %d not found".formatted(id)));
    }

    @CircuitBreaker(name = "bookServiceBreaker")
    @Override
    @Transactional
    public BookDto create(BookDto data) {
        var book = save(0, data.title(), data.author(), data.genres());
        return BookConverter.bookDtoOf(book);
    }

    @CircuitBreaker(name = "bookServiceBreaker")
    @Override
    @Transactional
    public void update(BookDto data) {
        var id = data.id();
        bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException("Book with id: %d not found".formatted(id)));
        save(data.id(), data.title(), data.author(), data.genres());
    }

    @CircuitBreaker(name = "bookServiceBreaker")
    @Override
    @Transactional
    public void deleteById(long id) {
        bookRepository.deleteById(id);
    }

    private Book save(long id, String title, long authorId, Set<Long> genresIds) {
        if (isEmpty(genresIds)) {
            throw new GenresException("Genres ids must not be null");
        }
        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new AuthorNotFoundException("Author with id %d not found".formatted(id)));
        var genres = genreRepository.findByIdIsIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new GenreNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }
        var book = new Book(id, title, author, genres);
        return bookRepository.save(book);
    }
}
