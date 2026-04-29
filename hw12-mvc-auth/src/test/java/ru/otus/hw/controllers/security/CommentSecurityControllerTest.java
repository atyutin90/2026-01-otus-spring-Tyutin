package ru.otus.hw.controllers.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.controllers.CommentController;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;

@TestInstance(PER_CLASS)
@DisplayName("Контроллеры для работы с комментариями к книге c учетом аутентификацией ")
@Import({SecurityConfiguration.class})
@WebMvcTest(CommentController.class)
public class CommentSecurityControllerTest extends AbstractSecurityControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CommentService commentService;

    private List<CommentDto> dtoComments;

    @BeforeEach
    void setUp() {
        var dbBooks = getDbBooks();
        dtoComments = getDbComments(dbBooks).stream().map(CommentConverter::commentDtoOf).toList();
    }

    @DisplayName("страница для создания нового комментария должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 200,",
        "user, 200,",
        "test, 302, http://localhost/login"
    })
    void shouldRenderNewEditPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var bookId = 1L;
        var expectedView = "/comment/edit";
        var requestBuilder = get("/books/%d/comments".formatted(bookId));

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("страница редактирования комментария должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 200,",
        "user, 200,",
        "test, 302, http://localhost/login"
    })
    void shouldRenderEditPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var id = 1L;
        var bookId = 1L;
        var comment = dtoComments.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);
        when(commentService.findById(id)).thenReturn(comment);

        var expectedView = "/comment/edit";
        var requestBuilder = get("/books/%d/comments/%d".formatted(bookId, id));

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("создание нового комментария и перенаправление на контекстный путь или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 302,",
        "user, 302,",
        "test, 302, http://localhost/login"
    })
    void shouldNewSaveCommentAndRedirectToContextPathOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var bookId = 1L;
        var expect = new CommentDto(null, NEW_COMMENT_TEXT);
        when(commentService.create(anyLong(), any(CommentDto.class))).thenReturn(expect);

        var expectedView = "redirect:/books/%d".formatted(bookId);
        var requestBuilder = post("/books/%d/comments".formatted(bookId))
            .param("text", expect.text());

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("редактирование комментария и перенаправление на контекстный путь или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 302,",
        "user, 302,",
        "test, 302, http://localhost/login"
    })
    void shouldSaveCommentAndRedirectToContextPathOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var bookId = 1L;
        var expect = new CommentDto(4L, NEW_COMMENT_TEXT);
        when(commentService.create(anyLong(), any(CommentDto.class))).thenReturn(expect);

        var expectedView = "redirect:/books/%d".formatted(bookId);
        var requestBuilder = post("/books/%d/comments".formatted(bookId))
            .param("id", expect.id().toString())
            .param("text", expect.text());

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("удаление комментария и перенаправление на контекстный путь или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 302,",
        "user, 302,",
        "test, 302, http://localhost/login"
    })
    void shouldDeleteCommentAndRedirectToContextPathOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {

        var id = 1L;
        var bookId = 1L;
        var expectedView = "redirect:/books/%d".formatted(bookId);
        var requestBuilder = delete("/books/%d/comments/%d".formatted(bookId, id));

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

}
