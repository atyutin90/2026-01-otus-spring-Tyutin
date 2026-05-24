package ru.otus.hw.services.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.mongo.BookDoc;
import ru.otus.hw.repositories.mongo.AuthorMongoRepository;
import ru.otus.hw.repositories.mongo.BookMongoRepository;
import ru.otus.hw.repositories.mongo.CommentMongoRepository;
import ru.otus.hw.repositories.mongo.GenreMongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

@RequiredArgsConstructor
@Service
public class BookMongoServiceImpl implements BookMongoService {
    private final AuthorMongoRepository authorRepository;

    private final GenreMongoRepository genreRepository;

    private final BookMongoRepository bookRepository;

    private final CommentMongoRepository commentRepository;

    @Override
    public Optional<BookDoc> findById(String id) {
        return bookRepository.findById(id);
    }

    @Override
    public List<BookDoc> findAll() {
        return bookRepository.findAll();
    }

    @Override
    @Transactional
    public BookDoc insert(String title, String authorId, Set<String> genresIds) {
        return save(null, title, authorId, genresIds);
    }

    @Override
    @Transactional
    public BookDoc update(String id, String title, String authorId, Set<String> genresIds) {
        return save(id, title, authorId, genresIds);
    }

    @Override
    @Transactional
    public void deleteById(String id) {
        commentRepository.deleteByBookId(id);
        bookRepository.deleteById(id);
    }

    private BookDoc save(String id, String title, String authorId, Set<String> genresIds) {
        if (isEmpty(genresIds)) {
            throw new IllegalArgumentException("Genres ids must not be null");
        }

        var author = authorRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("Author with id %s not found".formatted(authorId)));
        var genres = genreRepository.findByIdIsIn(genresIds);
        if (isEmpty(genres) || genresIds.size() != genres.size()) {
            throw new EntityNotFoundException("One or all genres with ids %s not found".formatted(genresIds));
        }

        var book = new BookDoc(id, title, author, genres);
        return bookRepository.save(book);
    }
}
