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
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.models.Comment;

import java.util.List;
import java.util.Set;

import static java.util.Locale.ENGLISH;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static ru.otus.hw.DataTest.NEW_BOOK_TITLE;
import static ru.otus.hw.DataTest.getDbAuthors;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Контроллеры для работы с книгами c учетом acl ")
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
public class BookSecurityControllerAclTest {
    @Autowired
    private MockMvc mvc;

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
    }

    @DisplayName("страница со списком книг должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderListPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        mvc.perform(
                get("/books")
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/book/list"))
            .andExpect(model().attribute("books", dtoBooks));
    }

    @DisplayName("страница c просмотром книг должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderShowPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        var id = 1L;
        var book = dtoBooks.stream().filter(it -> it.id().equals(id)).findFirst().orElse(null);
        var dtoComments = dbComments.stream()
            .filter(it -> it.getBook().getId() == id)
            .map(CommentConverter::commentDtoOf)
            .toList();
        mvc.perform(
                get("/books/%d".formatted(id))
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/book/show"))
            .andExpect(model().attribute("book", book))
            .andExpect(model().attribute("authors", dtoAuthors))
            .andExpect(model().attribute("genres", dtoGenres))
            .andExpect(model().attribute("comments", dtoComments));
    }

    @DisplayName("страница для создания новой книги должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        mvc.perform(
                get("/books/new")
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/book/edit"))
            .andExpect(model().attribute("book", new BookDto(null, null, null, null)))
            .andExpect(model().attribute("authors", dtoAuthors))
            .andExpect(model().attribute("genres", dtoGenres));
    }

    @DisplayName("страница редактирования книги должна отображаться для любого аутентифицированного пользователя")
    @ParameterizedTest
    @CsvSource({"admin", "user"})
    void shouldRenderEditPageWithCorrectViewAndModelAttributesByAllUsers(String userName) throws Exception {
        var id = 1L;
        var book = dtoBooks.stream()
            .filter(it -> it.id().equals(id))
            .findFirst()
            .orElse(null);
        mvc.perform(
                get("/books/%d/edit".formatted(id))
                    .with(user(userName))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/book/edit"))
            .andExpect(model().attribute("book", book))
            .andExpect(model().attribute("authors", dtoAuthors))
            .andExpect(model().attribute("genres", dtoGenres));
    }

    @DisplayName("создание новой книги может осуществляться только пользователем с ролью ADMIN")
    @Test
    void shouldNewSaveBookAndRedirectToContextPathByUserWithAdminRole() throws Exception {
        var expect = new BookDto(4L, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));
        mvc.perform(
                post("/books")
                    .param("title", expect.title())
                    .param("author", expect.author().toString())
                    .param("genres", expect.genres().toArray()[0].toString())
                    .param("genres", expect.genres().toArray()[1].toString())
                    .with(user("test").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/books/%d".formatted(expect.id())));
    }

    @DisplayName("создание новой книги не может осуществляться пользователем с ролью отличной от ADMIN")
    @Test
    void shouldNotNewSaveBookAndRedirectToContextPathByUserWithoutAdminRole() throws Exception {
        var expect = new BookDto(null, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));
        mvc.perform(
                post("/books")
                    .param("title", expect.title())
                    .param("author", expect.author().toString())
                    .param("genres", expect.genres().toArray()[0].toString())
                    .param("genres", expect.genres().toArray()[1].toString())
                    .with(user("test").roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/book/edit"))
            .andExpect(model().attribute("book", expect))
            .andExpect(model().attribute("authors", dtoAuthors))
            .andExpect(model().attribute("genres", dtoGenres))
            .andExpect(model().attribute("errorMessage", messageSource.getMessage("error.not-allowed-create-or-modify-record", null, ENGLISH)));
    }

    @DisplayName("редактирование книги может осуществляться только пользователем с ролью ADMIN")
    @Test
    void shouldSaveBookAndRedirectToContextPathByUserWithAdminRole() throws Exception {
        var expect = new BookDto(3L, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));
        mvc.perform(post("/books")
                .param("id", expect.id().toString())
                .param("title", expect.title())
                .param("author", expect.author().toString())
                .param("genres", expect.genres().toArray()[0].toString())
                .param("genres", expect.genres().toArray()[1].toString())
                .with(user("test").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/books/%d".formatted(expect.id())));
    }

    @DisplayName("редактирование книги не может осуществляться пользователем с ролью отличной от ADMIN")
    @Test
    void shouldNotSaveBookAndRedirectToContextPathByUserWithoutAdminRole() throws Exception {
        var expect = new BookDto(3L, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));
        mvc.perform(post("/books")
                .param("id", expect.id().toString())
                .param("title", expect.title())
                .param("author", expect.author().toString())
                .param("genres", expect.genres().toArray()[0].toString())
                .param("genres", expect.genres().toArray()[1].toString())
                .with(user("test").roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/book/edit"))
            .andExpect(model().attribute("book", expect))
            .andExpect(model().attribute("authors", dtoAuthors))
            .andExpect(model().attribute("genres", dtoGenres))
            .andExpect(model().attribute("errorMessage", messageSource.getMessage("error.not-allowed-create-or-modify-record", null, ENGLISH)));
    }

    @DisplayName("удаление книги может осуществляться только пользователем с ролью ADMIN")
    @Test
    void shouldDeleteBookAndRedirectToContextPathByUserWithAdminRole() throws Exception {
        var id = 3L;
        mvc.perform(
            delete("/books/%d".formatted(id))
                .with(user("test").roles("ADMIN"))
            )
            .andExpect(status().is3xxRedirection())
            .andExpect(view().name("redirect:/books"));
    }

    @DisplayName("удаление жанра не может осуществляться пользователем с ролью отличной от ADMIN")
    @Test
    void shouldNotDeleteBookAndRedirectToContextPathByUserWithoutAdminRole() throws Exception {
        var id = 3L;
        mvc.perform(
                delete("/books/%d".formatted(id))
                    .with(user("test").roles("USER"))
            )
            .andExpect(status().isOk())
            .andExpect(view().name("/book/list"))
            .andExpect(model().attribute("books", dtoBooks))
            .andExpect(model().attribute("errorMessage", messageSource.getMessage("error.not-allowed-delete-record", null, ENGLISH)));
    }
}
