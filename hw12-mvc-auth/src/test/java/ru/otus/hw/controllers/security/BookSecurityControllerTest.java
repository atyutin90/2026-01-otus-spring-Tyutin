package ru.otus.hw.controllers.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.BookController;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Comment;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static ru.otus.hw.DataTest.NEW_BOOK_TITLE;
import static ru.otus.hw.DataTest.getDbAuthors;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;
import static ru.otus.hw.DataTest.getDbGenres;

@TestInstance(PER_CLASS)
@DisplayName("Контроллеры для работы с книгами c учетом аутентификацией ")
@Import({SecurityConfiguration.class})
@WebMvcTest(BookController.class)
public class BookSecurityControllerTest extends AbstractSecurityControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private MessageSource messageSource;

    private List<BookDto> dtoBooks;
    private List<AuthorDto> dtoAuthors;
    private List<GenreDto> dtoGenres;
    private List<Comment> dbComments;

    @BeforeEach
    void setUp() {
        var dbBooks = getDbBooks();
        dtoBooks = dbBooks.stream().map(BookConverter::bookDtoOf).toList();
        dtoAuthors = getDbAuthors().stream().map(AuthorConverter::authorDtoOf).toList();
        dtoGenres = getDbGenres().stream().map(GenreConverter::genreDtoOf).toList();
        dbComments = getDbComments(dbBooks);
        when(genreService.findAll()).thenReturn(dtoGenres);
        when(authorService.findAll()).thenReturn(dtoAuthors);
    }

    @DisplayName("страница со списком книг должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 200,",
        "user, 200,",
        "test, 302, http://localhost/login"
    })
    void shouldRenderListPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var expectedView = "/book/list";
        var requestBuilder = get("/books");
        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("страница c просмотром книги должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 200,",
        "user, 200,",
        "test, 302, http://localhost/login"
    })
    void shouldRenderShowPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var id = 1L;
        var book = dtoBooks.stream()
            .filter(it -> it.id().equals(id))
            .findFirst().orElseThrow();
        var dtoComments = dbComments.stream()
            .filter(it -> it.getBook().getId() == id)
            .map(CommentConverter::commentDtoOf)
            .toList();
        when(bookService.findById(id)).thenReturn(book);
        when(commentService.findByBookId(id)).thenReturn(dtoComments);

        var expectedView = "/book/show";
        var requestBuilder = get("/books/%d".formatted(id));

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("страница для создания новой книги должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin,200,",
        "user,200,",
        "test,302,http://localhost/login"
    })
    void shouldRenderNewEditPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var expectedView = "/book/edit";
        var requestBuilder = get("/books/new");

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("страница редактирования книги должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 200,",
        "user, 200,",
        "test, 302, http://localhost/login"
    })
    void shouldRenderEditPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var id = 1L;
        var book = dtoBooks.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);
        when(bookService.findById(id)).thenReturn(book);

        var expectedView = "/book/edit";
        var requestBuilder = get("/books/%d/edit".formatted(id));

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("создание новой книги и перенаправление на контекстный путь или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 302,",
        "user, 302,",
        "test, 302, http://localhost/login"
    })
    void shouldNewSaveBookAndRedirectToContextPathOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var expect = new BookDto(4L, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));
        when(bookService.create(any(BookDto.class))).thenReturn(expect);

        var expectedView = "redirect:/books/%d".formatted(expect.id());
        var requestBuilder = post("/books")
            .param("title", expect.title())
            .param("author", expect.author().toString())
            .param("genres", expect.genres().toArray()[0].toString())
            .param("genres", expect.genres().toArray()[1].toString());

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("редактирование книги и перенаправление на контекстный путь или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 302,",
        "user, 302,",
        "test, 302, http://localhost/login"
    })
    void shouldSaveBookAndRedirectToContextPathOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var expect = new BookDto(3L, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));

        var expectedView = "redirect:/books/%d".formatted(expect.id());
        var requestBuilder = post("/books")
                .param("id", expect.id().toString())
                .param("title", expect.title())
                .param("author", expect.author().toString())
                .param("genres", expect.genres().toArray()[0].toString())
                .param("genres", expect.genres().toArray()[1].toString());

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("удаление книги и перенаправление на контекстный путь или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 302,",
        "user, 302,",
        "test, 302, http://localhost/login"
    })
    void shouldDeleteBookAndRedirectToContextPathOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var id = 3L;
        var expectedView = "redirect:/books";
        var requestBuilder = delete("/books/%d".formatted(id));

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }
}
