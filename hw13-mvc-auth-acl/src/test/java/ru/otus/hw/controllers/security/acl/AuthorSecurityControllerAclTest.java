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
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;

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
import static ru.otus.hw.DataTest.NEW_AUTHOR_FULL_NAME;
import static ru.otus.hw.DataTest.getDbAuthors;

@DisplayName("Контроллеры для работы с авторами c учетом acl ")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class AuthorSecurityControllerAclTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MessageSource messageSource;

    private List<AuthorDto> dtoAuthors;

    @BeforeEach
    void setUp() {
        dtoAuthors = getDbAuthors().stream().map(AuthorConverter::authorDtoOf).toList();
    }

    @DisplayName("страница со списком авторов должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderListPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        mvc.perform(
                get("/authors")
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/author/list"))
            .andExpect(model().attribute("authors", dtoAuthors));
    }

    @DisplayName("страница c просмотром авторов должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderShowPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        var id = 1L;
        var author = dtoAuthors.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);
        mvc.perform(
                get("/authors/%d".formatted(id))
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/author/show"))
            .andExpect(model().attribute("author", author));
    }

    @DisplayName("страница для создания нового автора должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        mvc.perform(
                get("/authors/new")
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/author/edit"))
            .andExpect(model().attribute("author", new AuthorDto(null, null)));
    }

    @DisplayName("страница редактирования автора должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderEditPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        var id = 1L;
        var author = dtoAuthors.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);
        mvc.perform(
                get("/authors/%d/edit".formatted(id))
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/author/edit"))
            .andExpect(model().attribute("author", author));
    }

    @DisplayName("создание нового автора может осуществляться только пользователем с ролью ADMIN")
    @Test
    void shouldNewSaveAuthorAndRedirectToContextPathByUserWithAdminRole() throws Exception {
        var expect = new AuthorDto(4L, NEW_AUTHOR_FULL_NAME);
        mvc.perform(
                post("/authors")
                    .param("fullName", expect.fullName())
                    .with(user("test").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/authors/%d".formatted(expect.id())));
    }

    @DisplayName("создание нового автора не может осуществляться пользователем с ролью отличной от ADMIN")
    @Test
    void shouldNotNewSaveAuthorAndRedirectToContextPathByUserWithoutAdminRole() throws Exception {
        var expect = new AuthorDto(null, NEW_AUTHOR_FULL_NAME);
        mvc.perform(
                post("/authors")
                    .param("fullName", expect.fullName())
                    .with(user("test").roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/author/edit"))
            .andExpect(model().attribute("author", expect))
            .andExpect(model().attribute("errorMessage", messageSource.getMessage("error.not-allowed-create-or-modify-record", null, ENGLISH)));
    }


    @DisplayName("редактирование автора может осуществляться только пользователем с ролью ADMIN")
    @Test
    void shouldSavAuthorAndRedirectToContextPathByUserWithAdminRole() throws Exception {
        var expect = new AuthorDto(3L, NEW_AUTHOR_FULL_NAME);
        mvc.perform(
                post("/authors")
                    .param("id", expect.id().toString())
                    .param("fullName", expect.fullName())
                    .with(user("test").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/authors/%d".formatted(expect.id())));
    }

    @DisplayName("редактирование автора не может осуществляться пользователем с ролью отличной от ADMIN")
    @Test
    void shouldNotSaveAuthorAndRedirectToContextPathByUserWithoutAdminRole() throws Exception {
        var expect = new AuthorDto(3L, NEW_AUTHOR_FULL_NAME);
        mvc.perform(
                post("/authors")
                    .param("id", expect.id().toString())
                    .param("fullName", expect.fullName())
                    .with(user("test").roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/author/edit"))
            .andExpect(model().attribute("author", expect))
            .andExpect(model().attribute("errorMessage", messageSource.getMessage("error.not-allowed-create-or-modify-record", null, ENGLISH)));
    }

    @DisplayName("удаление автора может осуществляться только пользователем с ролью ADMIN")
    @Test
    void shouldDeleteAuthorAndRedirectToContextPathByUserWithAdminRole() throws Exception {
        var id = 3L;
        mvc.perform(
                delete("/authors/%d".formatted(id))
                    .with(user("test").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/authors"));
    }

    @DisplayName("удаление жанра не может осуществляться пользователем с ролью отличной от ADMIN")
    @Test
    void shouldNotDeleteAuthorAndRedirectToContextPathByUserWithoutAdminRole() throws Exception {
        var id = 3L;
        mvc.perform(
                delete("/authors/%d".formatted(id))
                    .with(user("test").roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/author/list"))
            .andExpect(model().attribute("errorMessage", messageSource.getMessage("error.not-allowed-delete-record", null, ENGLISH)));
    }
}
