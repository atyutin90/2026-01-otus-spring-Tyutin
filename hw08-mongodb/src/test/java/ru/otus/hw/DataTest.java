package ru.otus.hw;

import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;
import java.util.stream.IntStream;

public final class DataTest {

    public static final String INVALID_ID = "-1";

    public static final String BOOK_ID = "1";

    public static final String COMMENT_ID = "2";

    public static final String DELETE_COMMENT_ID = "3";

    public static final String NEW_COMMENT_TEXT = "Comment_NEW";

    public static final String MODIFY_COMMENT_TEXT = "Comment_UNPDATE";

    public static final String NEW_BOOK_TITLE= "Comment_NEW";

    public static final String MODIFY_BOOK_TITLE = "Comment_UNPDATE";

    public static List<Author> getDbAuthors() {
        return IntStream.range(1, 4)
                .boxed()
                .map(id -> new Author(id.toString(), "Author_" + id))
                .toList();
    }

    public static List<Genre> getDbGenres() {
        return  IntStream.range(1, 7)
                .boxed()
                .map(id -> new Genre(id.toString(), "Genre_" + id))
                .toList();
    }

    public static List<Book> getDbBooks(List<Author> dbAuthors, List<Genre> dbGenres) {
        return IntStream.range(1, 4).boxed()
                .map(id -> new Book(id.toString(),
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
                .map(id -> new Comment(id.toString(), "Comment_" + id, books.stream().filter(it -> BOOK_ID.equals(it.getId())).findFirst().orElse(null)))
                .toList();
    }
}
