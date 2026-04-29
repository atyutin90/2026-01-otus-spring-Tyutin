package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Author;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Comment;
import ru.otus.hw.models.Genre;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.DataTest.BOOK_ID;
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;
import static ru.otus.hw.DataTest.getDbAuthors;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Репозиторий на основе JPA для работы с комментариями к книге ")
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    private CommentRepository repository;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
        dbComments = getDbComments(dbBooks);
    }

    @DisplayName("должен загружать комментарии к книге по id")
    @Test
    void shouldReturnCorrectCommentById() {
        var expected = dbComments.get(0);
        var comment = repository.findById(expected.getId());
        assertThat(comment).isPresent().get().isEqualTo(expected);
    }

    @DisplayName("должен загружать список всех комментариев для заданой книги по ее id")
    @Test
    void shouldReturnCorrectCommentList() {
        var comments = repository.findByBookId(BOOK_ID);
        var expected = dbComments.stream().filter(it -> it.getId() == BOOK_ID).toList();

        assertThat(comments).containsExactlyElementsOf(expected);
    }

    @DisplayName("должен сохранять новый комментарий к книге")
    @Test
    void shouldSaveNewComment() {
        var expected = new Comment(0, NEW_COMMENT_TEXT, dbBooks.get(2));
        var returnedComment = repository.save(expected);
        assertThat(returnedComment).isNotNull()
                .matches(comment -> comment.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected);

        assertThat(repository.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен сохранять измененный комментарий к книге")
    @Test
    void shouldSaveUpdatedComment() {
        var expected = new Comment(2L, NEW_COMMENT_TEXT, dbBooks.get(2));

        assertThat(repository.findById(expected.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expected);

        var returnedComment = repository.save(expected);
        assertThat(returnedComment).isNotNull()
                .matches(book -> book.getId() > 0)
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expected);

        assertThat(repository.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен удалять комментарий к книге по id ")
    @Test
    void shouldDeleteComment() {
        var id = 3L;
        assertThat(repository.findById(id)).isPresent();
        repository.deleteById(id);
        assertThat(repository.findById(id)).isEmpty();
    }

}
