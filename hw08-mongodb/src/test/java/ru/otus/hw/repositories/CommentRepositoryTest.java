package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.otus.hw.MongoDataTestDataInitializer;
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

@DisplayName("Репозиторий для работы с комментариями к книге ")
public class CommentRepositoryTest extends AbstractRepositoryTest{

    @Autowired
    private MongoDataTestDataInitializer initializer;

    @Autowired
    private CommentRepository repository;

    private List<Author> dbAuthors;

    private List<Genre> dbGenres;

    private List<Book> dbBooks;

    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        //перед каждым тестом пересоздаем данные, чтобы не получить коллизии
        initializer.init();
        dbAuthors = getDbAuthors();
        dbGenres = getDbGenres();
        dbBooks = getDbBooks(dbAuthors, dbGenres);
        dbComments = getDbComments(dbBooks);
    }

    @DisplayName("должен загружать комментарии к книге по id")
    @Test
    void shouldReturnCorrectCommentById() {
        var expectedComment = dbComments.get(0);
        var comment = repository.findById(expectedComment.getId());
        assertThat(comment).isPresent().get().isEqualTo(expectedComment);
    }

    @DisplayName("должен загружать список всех комментариев для заданой книги по ее id")
    @Test
    void shouldReturnCorrectCommentList() {
        var comments = repository.findByBookId(BOOK_ID);
        var expectedComments = dbComments.stream().filter(it -> BOOK_ID.equals(it.getBook().getId())).toList();

        assertThat(comments).containsExactlyElementsOf(expectedComments);
    }

    @DisplayName("должен сохранять новый комментарий к книге")
    @Test
    void shouldSaveNewComment() {
        var expectedComment = new Comment("0", NEW_COMMENT_TEXT, dbBooks.get(2));
        var returnedComment = repository.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(repository.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен сохранять измененный комментарий к книге")
    @Test
    void shouldSaveUpdatedComment() {
        var expectedComment = new Comment("2", NEW_COMMENT_TEXT, dbBooks.get(2));

        assertThat(repository.findById(expectedComment.getId()))
                .isPresent()
                .get()
                .isNotEqualTo(expectedComment);

        var returnedComment = repository.save(expectedComment);
        assertThat(returnedComment).isNotNull()
                .usingRecursiveComparison().ignoringExpectedNullFields().isEqualTo(expectedComment);

        assertThat(repository.findById(returnedComment.getId()))
                .isPresent()
                .get()
                .isEqualTo(returnedComment);
    }

    @DisplayName("должен удалять комментарий к книге по id ")
    @Test
    void shouldDeleteComment() {
        var id = "3";
        assertThat(repository.findById(id)).isPresent();
        repository.deleteById(id);
        assertThat(repository.findById(id)).isEmpty();
    }

}
