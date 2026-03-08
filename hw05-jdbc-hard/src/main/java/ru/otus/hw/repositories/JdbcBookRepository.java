package ru.otus.hw.repositories;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.exceptions.EntityNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.util.Collections.singletonMap;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;

@Repository
@RequiredArgsConstructor
public class JdbcBookRepository implements BookRepository {

    private final GenreRepository genreRepository;

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Optional<Book> findById(long id) {
        var sql = """
                select b.id, b.title, b.author_id, a.full_name as author_full_name, g.id as genre_id,
                       g.name as genre_name
                from books b
                    left join authors a on b.author_id = a.id
                    left join books_genres bg on b.id = bg.book_id
                    left join genres g on bg.genre_id = g.id
                where b.id = :id
                """;
        var params = singletonMap("id", id);
        return ofNullable(jdbc.query(sql, params, new BookResultSetExtractor()));
    }

    @Override
    public List<Book> findAll() {
        var genres = genreRepository.findAll();
        var books = getAllBooksWithoutGenres();
        var relations = getAllGenreRelations();
        mergeBooksInfo(books, genres, relations);
        return books;
    }

    @Transactional
    @Override
    public Book save(Book book) {
        if (book.getId() == 0) {
            return insert(book);
        }
        return update(book);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        var sql = "delete from books where id = :id";
        var params = singletonMap("id", id);
        jdbc.update(sql, params);
    }

    private List<Book> getAllBooksWithoutGenres() {
        var sql = """
                select b.id, b.title, b.author_id, a.full_name as author_full_name
                from books b
                left join authors a on b.author_id = a.id
                """;
        return jdbc.query(sql, new BookRowMapper());
    }

    private List<BookGenreRelation> getAllGenreRelations() {
        var sql = "select book_id, genre_id from books_genres";
        return jdbc.query(sql, new BookGenreRelationMapper());
    }

    private void mergeBooksInfo(List<Book> booksWithoutGenres, List<Genre> genres,
                                List<BookGenreRelation> relations) {
        var genreMap = genres.stream().collect(Collectors.toMap(Genre::getId, identity()));
        var bookMap = booksWithoutGenres.stream().collect(Collectors.toMap(Book::getId, identity()));
        if (relations != null) {
            relations.forEach(relation -> {
                var book = bookMap.get(relation.bookId);
                var genre = genreMap.get(relation.genreId);
                if (book != null && genre != null) {
                    book.addGenre(genre);
                }
            });
        }
    }

    private Book insert(Book book) {
        var keyHolder = new GeneratedKeyHolder();
        var sql = "insert into books(title, author_id) values (:title, :authorId)";
        var params = new MapSqlParameterSource();
        params.addValue("title", book.getTitle());
        params.addValue("authorId", book.getAuthor() != null ? book.getAuthor().getId() : null);
        jdbc.update(sql, params, keyHolder);
        book.setId(keyHolder.getKeyAs(Long.class));
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private Book update(Book book) {
        var sql = "update books set title = :title, author_id = :authorId where id = :id";
        var params = new MapSqlParameterSource();
        params.addValue("id", book.getId());
        params.addValue("title", book.getTitle());
        params.addValue("authorId", book.getAuthor() != null ? book.getAuthor().getId() : null);
        int result = jdbc.update(sql, params);
        if (result <= 0) {
            throw new EntityNotFoundException(format("Book with id=%d not found", book.getId()));
        }
        removeGenresRelationsFor(book);
        batchInsertGenresRelationsFor(book);
        return book;
    }

    private void batchInsertGenresRelationsFor(Book book) {
        if (book.getGenres() != null && !book.getGenres().isEmpty()) {
            var sql = "insert into books_genres(book_id, genre_id) values (:bookId, :genreId)";
            var params = book.getGenres().stream()
                    .map(genre ->
                            new MapSqlParameterSource()
                                    .addValue("bookId", book.getId())
                                    .addValue("genreId", genre.getId())
                    ).toArray(MapSqlParameterSource[]::new);
            jdbc.batchUpdate(sql, params);
        }
    }

    private void removeGenresRelationsFor(Book book) {
        if (book.getGenres() != null && !book.getGenres().isEmpty()) {
            var sql = "delete from books_genres where book_id = :bookId";
            var params = singletonMap("bookId", book.getId());
            jdbc.update(sql, params);
        }
    }

    private static class BookRowMapper implements RowMapper<Book> {

        @Override
        public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
            var id = rs.getLong("id");
            var title = rs.getString("title");
            var authorId = rs.getLong("author_id");
            var authorFullName = rs.getString("author_full_name");
            var author = new Author(authorId, authorFullName);
            return new Book(id, title, author, new ArrayList<>());
        }
    }

    @SuppressWarnings("ClassCanBeRecord")
    @RequiredArgsConstructor
    private static class BookResultSetExtractor implements ResultSetExtractor<Book> {

        @Override
        public Book extractData(ResultSet rs) throws SQLException, DataAccessException {
            Map<Long, Book> bookMap = new HashMap<>();
            while (rs.next()) {
                var id = rs.getLong("id");
                var title = rs.getString("title");
                var authorId = rs.getLong("author_id");
                var authorFullName = rs.getString("author_full_name");
                var genreId = rs.getLong("genre_id");
                var genreName = rs.getString("genre_name");
                var author = new Author(authorId, authorFullName);
                var book = bookMap.computeIfAbsent(id, b -> new Book(id, title, author, new ArrayList<>()));
                var genre = new Genre(genreId, genreName);
                book.addGenre(genre);
            }
            return bookMap.values().stream().findFirst().orElse(null);
        }
    }

    private record BookGenreRelation(long bookId, long genreId) {
    }

    private static class BookGenreRelationMapper implements RowMapper<BookGenreRelation> {
        @Override
        public BookGenreRelation mapRow(ResultSet rs, int rowNum) throws SQLException {
            long bookId = rs.getLong("book_id");
            long genreId = rs.getLong("genre_id");
            return new BookGenreRelation(bookId, genreId);
        }
    }
}
