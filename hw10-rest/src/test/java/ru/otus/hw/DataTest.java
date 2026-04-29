package ru.otus.hw;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

public final class DataTest {

    public static final long BOOK_ID = 1L;

    public static final long COMMENT_ID = 2L;

    public static final long DELETE_COMMENT_ID = 3L;

    public static final String NEW_AUTHOR_FULL_NAME = "Author_NEW";

    public static final String NEW_GENDER_TEXT = "Genre_NEW";

    public static final String NEW_COMMENT_TEXT = "Comment_NEW";

    public static final String MODIFY_COMMENT_TEXT = "Comment_UNPDATE";

    public static final String NEW_BOOK_TITLE= "Comment_NEW";

    public static final String INVALID_PARAMS = "test";

    public static List<Author> getDbAuthors() {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Author(id, "Author_" + id))
                .toList();
    }

    public static List<Genre> getDbGenres() {
        return IntStream.range(1, 7).boxed()
                .map(id -> new Genre(id, "Genre_" + id))
                .toList();
    }

    public static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id,
                        "BookTitle_" + id,
                        dbAuthors.get(id - 1),
                        dbGenres.subList((id - 1) * 2, (id - 1) * 2 + 2)
                ))
                .toList();
    }

    public static List<Book> getDbBooks() {
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        return getDbBooks(dbAuthors, dbGenres);
    }

    public static List<Comment> getDbComments(List<Book> books) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Comment(id, "Comment_" + id, books.get(id - 1)))
                .toList();
    }
}
