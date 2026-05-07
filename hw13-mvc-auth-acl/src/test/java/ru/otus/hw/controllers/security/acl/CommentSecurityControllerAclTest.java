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
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;

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
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;

@DisplayName("Контроллеры для работы с комментариями к книге c учетом acl ")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class CommentSecurityControllerAclTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private MessageSource messageSource;

    private List<CommentDto> dtoComments;

    @BeforeEach
    void setUp() {
        var dbBooks = getDbBooks();
        dtoComments = getDbComments(dbBooks).stream().map(CommentConverter::commentDtoOf).toList();
    }

    @DisplayName("страница для создания нового комментария к книге должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        var bookId = 1L;
        mvc.perform(
                get("/books/%d/comments".formatted(bookId))
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/comment/edit"))
            .andExpect(model().attribute("comment", new CommentDto(null, null)));
    }

    @DisplayName("страница редактирования комментария к книге должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderEditPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        var id = 1L;
        var bookId = 1L;
        var comment = dtoComments.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);
        mvc.perform(
                get("/books/%d/comments/%d".formatted(bookId, id))
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/comment/edit"))
            .andExpect(model().attribute("comment", comment));
    }

    @DisplayName("создание нового комментария к книге может осуществляться любым аутентифицированным пользователем")
    @Test
    void shouldNewSaveCommentAndRedirectToContextPathByAllUsers() throws Exception {
        var bookId = 1L;
        var expect = new CommentDto(null, NEW_COMMENT_TEXT);
        mvc.perform(
                post("/books/%d/comments".formatted(bookId))
                    .param("text", expect.text())
                    .with(user("test").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/books/%d".formatted(bookId)));
    }

    @DisplayName("редактирование комментария к книге может осуществляться пользователь который его создал")
    @Test
    void shouldSaveCommentAndRedirectToContextPathByUserWhoCreatedIt() throws Exception {
        var bookId = 3L;
        var expect = new CommentDto(3L, NEW_COMMENT_TEXT);
        mvc.perform(post("/books/%d/comments".formatted(bookId))
                .param("id", expect.id().toString())
                .param("text", expect.text())
                .with(user("user").roles("USER"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/books/%d".formatted(bookId)));
    }

    @DisplayName("редактирование комментария к книге может осуществляться пользователь с ролью ADMIN")
    @Test
    void shouldSaveCommentAndRedirectToContextPathByUserWithAdminRole() throws Exception {
        var bookId = 3L;
        var expect = new CommentDto(3L, NEW_COMMENT_TEXT);
        mvc.perform(post("/books/%d/comments".formatted(bookId))
                .param("id", expect.id().toString())
                .param("text", expect.text())
                .with(user("test").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/books/%d".formatted(bookId)));
    }

    @DisplayName("редактирование комментария к книге не может осуществляться пользователь с ролью отличной от ADMIN или пользователем который не создавал данный комментарий")
    @Test
    void shouldNotSaveCommentAndRedirectToContextPath() throws Exception {
        var bookId = 3L;
        var expect = new CommentDto(3L, NEW_COMMENT_TEXT);
        mvc.perform(post("/books/%d/comments".formatted(bookId))
                .param("id", expect.id().toString())
                .param("text", expect.text())
                .with(user("test").roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/comment/edit"))
            .andExpect(model().attribute("comment", expect))
            .andExpect(model().attribute("errorMessage", messageSource.getMessage("error.not-allowed-create-or-modify-record", null, ENGLISH)));
    }

    @DisplayName("удаление комментария к книге может осуществляться пользователь который его создал")
    @Test
    void shouldDeleteCommentAndRedirectToContextPathByUserWhoCreatedIt() throws Exception {
        var id = 3L;
        var bookId = 3L;
        mvc.perform(
                delete("/books/%d/comments/%d".formatted(bookId, id))
                    .with(user("user").roles("USER"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/books/%d".formatted(bookId)));
    }

    @DisplayName("удаление комментария к книге может осуществляться пользователь с ролью ADMIN")
    @Test
    void shouldDeleteCommentAndRedirectToContextPathByUserWithAdminRole() throws Exception {
        var id = 3L;
        var bookId = 3L;
        mvc.perform(
                delete("/books/%d/comments/%d".formatted(bookId, id))
                    .with(user("test").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/books/%d".formatted(bookId)));
    }


    @DisplayName("удаление комментария к книге не может осуществляться пользователь с ролью отличной от ADMIN или пользователем который не создавал данный комментарий")
    @Test
    void shouldNotDeleteCommentAndRedirectToContextPath() throws Exception {
        var id = 3L;
        var bookId = 3L;
        mvc.perform(
                delete("/books/%d/comments/%d".formatted(bookId, id))
                    .with(user("test").roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/comment/edit"))
            .andExpect(model().attribute("errorMessage", messageSource.getMessage("error.not-allowed-delete-record", null, ENGLISH)));
    }
}
