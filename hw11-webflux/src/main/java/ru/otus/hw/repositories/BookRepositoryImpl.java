package ru.otus.hw.repositories;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.otus.hw.models.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;

@Repository
@RequiredArgsConstructor
@Slf4j
public class BookRepositoryImpl implements BookRepository {

    private static final String ID = "id";

    private static final String BOOK_ID = "bookId";

    private static final String GENRE_ID = "genreId";

    private final R2dbcEntityOperations ops;

    @Override
    public Mono<Book> findById(long id) {
        var sql = """
            select b.id, b.title, b.author_id, bg.genre_id as genre_id
            from books b left join books_genres bg on b.id = bg.book_id
            where b.id = :id
            """;
        return ops.getDatabaseClient()
            .sql(sql)
            .bind(ID, id)
            .map(mapRow())
            .all()
            .collectList()
            .flatMap(this::bookOf);
    }

    @Override
    public Flux<Book> findAll() {
        var books = ops.select(Book.class).all();
        var bookGenreMap = getBookGenreMap();
        return mergeBooksInfo(books, bookGenreMap);
    }

    @Transactional
    @Override
    public Mono<Book> save(Book book) {
        return book.getId() == 0 ? insert(book) : update(book);
    }

    @Transactional
    @Override
    public Mono<Void> deleteById(long id) {
        var sql = "delete from books where id = :id";
        return removeGenresRelationsFor(id)
            .then(ops.getDatabaseClient()
                .sql(sql)
                .bind(ID, id)
                .fetch()
                .rowsUpdated())
            .then();
    }

    private Mono<Map<Long, List<Long>>> genreMapOf(List<Pair<Long, Long>> pairs) {
        Map<Long, List<Long>> bookGenreMap = new HashMap<>();
        for (Pair<Long, Long> pair : pairs) {
            bookGenreMap.computeIfAbsent(pair.getFirst(), k -> new ArrayList<>());
            bookGenreMap.get(pair.getFirst()).add(pair.getSecond());
        }
        return Mono.just(bookGenreMap);
    }

    private Mono<Map<Long, List<Long>>> getBookGenreMap() {
        var sql = "select book_id, genre_id from books_genres";
        return ops.getDatabaseClient().sql(sql)
            .map((row, rowMetadata) -> Pair.of(
                row.get("book_id", Long.class),
                row.get("genre_id", Long.class))
            )
            .all()
            .collectList()
            .flatMap(this::genreMapOf);
    }

    private Flux<Book> mergeBooksInfo(Flux<Book> books,
                                      Mono<Map<Long, List<Long>>> bookGenreMap) {
        return books.flatMap(book ->
            bookGenreMap.map(map -> {
                    var genreIds = map.get(book.getId());
                    if (isNotEmpty(genreIds)) {
                        genreIds.forEach(book::addGenreId);
                    }
                    return book;
                }
            )
        );
    }

    private Mono<Book> bookOf(List<BookRow> bookRows) {
        var result = Mono.<Book>empty();
        if (isNotEmpty(bookRows)) {
            var bookMap = new HashMap<Long, Book>();
            for (BookRow rs : bookRows) {
                var book = bookMap.computeIfAbsent(
                    rs.bookId,
                    id -> new Book(rs.bookId, rs.title, rs.authorId, new ArrayList<>())
                );
                book.addGenreId(rs.genreId);
            }
            result = bookMap.values().stream()
                .findFirst()
                .map(Mono::just)
                .orElse(result);
        }
        return result;
    }

    private Mono<Book> insert(Book data) {
        return ops.insert(data)
            .flatMap(book -> batchInsertGenresRelationsFor(book.getId(), book.getGenresIds())
                .thenReturn(book.getId())
            ).map(bookId -> {
                data.setId(bookId);
                return data;
            });
    }

    private Mono<Book> update(Book data) {
        return ops.update(data)
            .flatMap(book -> removeGenresRelationsFor(book.getId())
                .thenReturn(book.getId())
            ).flatMap(bookId -> batchInsertGenresRelationsFor(bookId, data.getGenresIds())
                .thenReturn(data));
    }

    private Mono<Void> batchInsertGenresRelationsFor(Long bookId, List<Long> genreIds) {
        var result = Mono.<Void>empty();
        if (isNotEmpty(genreIds)) {
            var sql = "insert into books_genres(book_id, genre_id) values (:bookId, :genreId)";
            result = Flux.fromIterable(genreIds)
                .flatMap(genreId -> ops.getDatabaseClient()
                    .sql(sql)
                    .bind(BOOK_ID, bookId)
                    .bind(GENRE_ID, genreId)
                    .fetch()
                    .rowsUpdated()
                ).then();
        }
        return result;
    }

    private Mono<Void> removeGenresRelationsFor(Long bookId) {
        var sql = "delete from books_genres where book_id = :bookId";
        return ops.getDatabaseClient()
            .sql(sql)
            .bind(BOOK_ID, bookId)
            .fetch()
            .rowsUpdated()
            .then();
    }

    private static BiFunction<Row, RowMetadata, BookRow> mapRow() {
        return (row, metadata) -> {
            var id = row.get("id", Long.class);
            var title = row.get("title", String.class);
            var authorId = row.get("author_id", Long.class);
            var genreId = row.get("genre_id", Long.class);
            return new BookRow(id, title, authorId, genreId);
        };
    }

    private record BookRow(
        Long bookId,
        String title,
        Long authorId,
        Long genreId
    ) {
    }
}
