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
import ru.otus.hw.controllers.GenreController;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.security.SecurityConfiguration;
import ru.otus.hw.services.GenreService;

import java.util.List;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static ru.otus.hw.DataTest.NEW_GENDER_TEXT;
import static ru.otus.hw.DataTest.getDbGenres;

@TestInstance(PER_CLASS)
@DisplayName("Контроллеры для работы с жанрами c учетом аутентификацией ")
@Import({SecurityConfiguration.class})
@WebMvcTest(GenreController.class)
public class GenreSecurityControllerTest extends AbstractSecurityControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private GenreService genreService;

    private List<GenreDto> dtoGenres;

    @BeforeEach
    void setUp() {
        dtoGenres = getDbGenres().stream().map(GenreConverter::genreDtoOf).toList();
    }

    @DisplayName("страница со списком жанров должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 200,",
        "user, 200,",
        "test, 302, http://localhost/login"
    })
    void shouldRenderListPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var expectedView = "/genre/list";
        var requestBuilder = get("/genres");
        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("страница c просмотром жанров должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 200,",
        "user, 200,",
        "test, 302, http://localhost/login"
    })
    void shouldRenderShowPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var id = 1L;
        var genre = dtoGenres.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);
        when(genreService.findById(id)).thenReturn(genre);

        var requestBuilder = get("/genres/%d".formatted(id));
        var expectedView = "/genre/show";

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("страница для создания нового жанра должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 200,",
        "user, 200,",
        "test, 302, http://localhost/login"
    })
    void shouldRenderNewEditPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var expectedView = "/genre/edit";
        var requestBuilder = get("/genres/new");

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("страница редактирования жанра должна отображаться или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 200,",
        "user, 200,",
        "test, 302 ,http://localhost/login,"
    })
    void shouldRenderEditPageWithCorrectViewOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var id = 1L;
        var genre = dtoGenres.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);
        when(genreService.findById(id)).thenReturn(genre);

        var expectedView = "/genre/edit";
        var requestBuilder = get("/genres/%d/edit".formatted(id));

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("создание нового жанра и перенаправление на контекстный путь или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 302,",
        "user, 302,",
        "test, 302, http://localhost/login"
    })
    void shouldNewSaveGenreAndRedirectToContextPathOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var expect = new GenreDto(7L, NEW_GENDER_TEXT);
        when(genreService.create(any(GenreDto.class))).thenReturn(expect);

        var expectedView = "redirect:/genres/%d".formatted(expect.id());
        var requestBuilder = post("/genres").param("name", expect.name());

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("редактирование жанра и перенаправление на контекстный путь или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 302,",
        "user, 302,",
        "test, 302, http://localhost/login,"
    })
    void shouldSaveGenreAndRedirectToContextPathOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {

        var expect = new GenreDto(3L, NEW_GENDER_TEXT);
        var expectedView = "redirect:/genres/%d".formatted(expect.id());

        var requestBuilder = post("/genres")
            .param("id", expect.id().toString())
            .param("name", expect.name());

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }

    @DisplayName("удаление жанра и перенаправление на контекстный путь или должен произойти редирект на страницу аутентификации")
    @ParameterizedTest
    @CsvSource({
        "admin, 302,",
        "user, 302,",
        "test, 302, http://localhost/login"
    })
    void shouldDeleteGenreAndRedirectToContextPathOrRedirectToLoginPage(String userName, int httpStatus, String redirectUrl) throws Exception {
        var id = 3L;

        var expectedView = "redirect:/genres";
        var requestBuilder = delete("/genres/%d".formatted(id));

        verifyMvcSecurity(mvc, requestBuilder, userName, httpStatus, redirectUrl, expectedView);
    }
}
