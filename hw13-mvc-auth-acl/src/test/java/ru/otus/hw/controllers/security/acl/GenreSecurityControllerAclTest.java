package ru.otus.hw.controllers.security.acl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;

import java.util.List;

import static java.util.Locale.ENGLISH;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static ru.otus.hw.DataTest.NEW_GENDER_TEXT;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Контроллеры для работы с жанрами c учетом acl ")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class GenreSecurityControllerAclTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MessageSource messageSource;

    private List<GenreDto> dtoGenres;

    @BeforeEach
    void setUp() {
        dtoGenres = getDbGenres().stream().map(GenreConverter::genreDtoOf).toList();
    }

    @DisplayName("страница со списком жанров должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderListPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        mvc.perform(
                get("/genres")
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/genre/list"))
            .andExpect(model().attribute("genres", dtoGenres));
    }

    @DisplayName("страница c просмотром жанров должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderShowPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        var id = 1L;
        var genre = dtoGenres.stream().filter(it -> it.id().equals(id)).findFirst().orElse(null);
        mvc.perform(
                get("/genres/%d".formatted(id))
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/genre/show"))
            .andExpect(model().attribute("genre", genre));
    }

    @DisplayName("страница для создания нового жанра должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        mvc.perform(
                get("/genres/new")
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/genre/edit"))
            .andExpect(model().attribute("genre", new GenreDto(null, null)));
    }

    @DisplayName("страница редактирования жанра должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderEditPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        var id = 1L;
        var genre = dtoGenres.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);
        mvc.perform(
                get("/genres/%d/edit".formatted(id))
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/genre/edit"))
            .andExpect(model().attribute("genre", genre));
    }

    @DisplayName("создание нового жанра может осуществляться только пользователем с ролью ADMIN")
    @Test
    void shouldNewSaveGenreAndRedirectToContextPathByUserWithAdminRole() throws Exception {
        var expect = new GenreDto(7L, NEW_GENDER_TEXT);
        mvc.perform(
                post("/genres")
                    .param("name", expect.name())
                    .with(user("test").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/genres/%d".formatted(expect.id())));
    }

    @DisplayName("создание нового жанра не может осуществляться пользователем с ролью отличной от ADMIN")
    @Test
    void shouldNotNewSaveGenreAndRedirectToContextPathByUserWithoutAdminRole() throws Exception {
        var expect = new GenreDto(null, NEW_GENDER_TEXT);
        mvc.perform(
                post("/genres")
                    .param("name", expect.name())
                    .with(user("test").roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/genre/edit"))
            .andExpect(model().attribute("genre", expect))
            .andExpect(model().attribute("errorMessage", messageSource.getMessage("error.not-allowed-create-or-modify-record", null, ENGLISH)));
    }

    @DisplayName("редактирование жанра может осуществляться только пользователем с ролью ADMIN")
    @Test
    void shouldSaveGenreAndRedirectToContextPathByUserWithAdminRole() throws Exception {
        var expect = new GenreDto(3L, NEW_GENDER_TEXT);
        mvc.perform(
                post("/genres")
                    .param("id", expect.id().toString())
                    .param("name", expect.name())
                    .with(user("test").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/genres/%d".formatted(expect.id())));
    }

    @DisplayName("редактирование жанра не может осуществляться пользователем с ролью отличной от ADMIN")
    @Test
    void shouldNotSaveGenreAndRedirectToContextPathByUserWithoutAdminRole() throws Exception {
        var expect = new GenreDto(3L, NEW_GENDER_TEXT);
        mvc.perform(
                post("/genres")
                    .param("id", expect.id().toString())
                    .param("name", expect.name())
                    .with(user("test").roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/genre/edit"))
            .andExpect(model().attribute("genre", expect))
            .andExpect(model().attribute("errorMessage", messageSource.getMessage("error.not-allowed-create-or-modify-record", null, ENGLISH)));
    }

    @DisplayName("удаление жанра может осуществляться только пользователем с ролью ADMIN")
    @Test
    void shouldDeleteGenreAndRedirectToContextPathByUserWithAdminRole() throws Exception {
        var id = 3L;
        mvc.perform(
                delete("/genres/%d".formatted(id))
                    .with(user("test").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/genres"));
    }

    @DisplayName("удаление жанра не может осуществляться пользователем с ролью отличной от ADMIN")
    @Test
    void shouldNotDeleteGenreAndRedirectToContextPathByUserWithoutAdminRole() throws Exception {
        var id = 3L;
        mvc.perform(
                delete("/genres/%d".formatted(id))
                    .with(user("test").roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/genre/list"))
            .andExpect(model().attribute("errorMessage", messageSource.getMessage("error.not-allowed-delete-record", null, ENGLISH)));
    }
}
