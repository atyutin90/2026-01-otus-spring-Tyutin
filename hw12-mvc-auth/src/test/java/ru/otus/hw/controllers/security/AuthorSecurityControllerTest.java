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
import ru.otus.hw.controllers.AuthorController;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static ru.otus.hw.DataTest.NEW_AUTHOR_FULL_NAME;
import static ru.otus.hw.DataTest.getDbAuthors;

@TestInstance(PER_CLASS)
@DisplayName("Контроллеры для работы с авторами c учетом аутентификацией ")
@Import({SecurityConfiguration.class})
@WebMvcTest(AuthorController.class)
public class AuthorSecurityControllerTest extends AbstractSecurityControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AuthorService authorService;

    private List<AuthorDto> dtoAuthors;

    @BeforeEach
    void setUp() {
        dtoAuthors = getDbAuthors().stream().map(AuthorConverter::authorDtoOf).toList();
    }


    @DisplayName("страница со списком авторов должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin,200,",
        "user,200,",
        "test,302,http://localhost/login"
    })
    void shouldRenderListPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var expectedView = "/author/list";
        var requestBuilder = get("/authors");
        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("страница c просмотром автора должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin,200,",
        "user,200,",
        "test,302,http://localhost/login"
    })
    void shouldRenderShowPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var id = 1L;
        var author = dtoAuthors.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);
        when(authorService.findById(id)).thenReturn(author);

        var expectedView = "/author/show";
        var requestBuilder = get("/authors/%d".formatted(id));

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("страница для создания нового автора должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin,200,",
        "user,200,",
        "test,302,http://localhost/login"
    })
    void shouldRenderNewEditPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var expectedView = "/author/edit";
        var requestBuilder = get("/authors/new");

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("страница редактирования автора должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin,200,",
        "user,200,",
        "test,302,http://localhost/login"
    })
    void shouldRenderEditPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var id = 1L;
        var author = dtoAuthors.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);
        when(authorService.findById(id)).thenReturn(author);

        var expectedView = "/author/edit";
        var requestBuilder = get("/authors/%d/edit".formatted(id));

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("создание нового автора и перенаправление на контекстный путь или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin,302,",
        "user,302,",
        "test,302,http://localhost/login"
    })
    void shouldNewSaveAuthorAndRedirectToContextPathOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var expect = new AuthorDto(4L, NEW_AUTHOR_FULL_NAME);
        when(authorService.create(any(AuthorDto.class))).thenReturn(expect);

        var expectedView = "redirect:/authors/%d".formatted(expect.id());
        var requestBuilder = post("/authors")
            .param("fullName", expect.fullName());

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("редактирование автора и перенаправление на контекстный путь или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin,302,",
        "user,302,",
        "test,302,http://localhost/login"
    })
    void shouldSaveAuthorAndRedirectToContextPathOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var expect = new AuthorDto(3L, NEW_AUTHOR_FULL_NAME);

        var expectedView = "redirect:/authors/%d".formatted(expect.id());
        var requestBuilder = post("/authors")
            .param("id", expect.id().toString())
            .param("fullName", expect.fullName());

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("удаление автора и перенаправление на контекстный путь или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin,302,",
        "user,302,",
        "test,302,http://localhost/login"
    })
    void shouldDeleteAuthorAndRedirectToContextPathOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var id = 3L;

        var expectedView = "redirect:/authors";
        var requestBuilder = delete("/authors/%d".formatted(id));

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }
}
