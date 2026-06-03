package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.services.AuthorService;

import java.util.List;

import static java.lang.Long.MAX_VALUE;
import static java.util.Locale.ENGLISH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static ru.otus.hw.DataTest.NEW_AUTHOR_FULL_NAME;
import static ru.otus.hw.DataTest.getDbAuthors;

@DisplayName("Контроллеры для работы с авторами ")
@WebMvcTest(AuthorController.class)
@Import({ResourceBundleMessageSource.class})
public class AuthorControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AuthorService authorService;

    @Autowired
    private MessageSource messageSource;

    private List<AuthorDto> dtoAuthors;

    @BeforeEach
    void setUp() {
        dtoAuthors = getDbAuthors().stream().map(AuthorConverter::authorDtoOf).toList();
    }

    @DisplayName("страница со списком авторов должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() throws Exception {
        when(authorService.findAll()).thenReturn(dtoAuthors);
        mvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("/author/list"))
                .andExpect(model().attribute("authors", dtoAuthors));
    }

    @DisplayName("страница c просмотром автора должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderShowPageWithCorrectViewAndModelAttributes() throws Exception {
        var id = 1L;
        var author = dtoAuthors.stream()
                .filter(it -> it.id().equals(id))
                .findFirst()
                .orElse(null);
        when(authorService.findById(id)).thenReturn(author);
        mvc.perform(get("/authors/%d".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(view().name("/author/show"))
                .andExpect(model().attribute("author", author));
        verify(authorService, times(1)).findById(id);
    }

    @DisplayName("страница для создания нового автора должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributes() throws Exception {
        mvc.perform(get("/authors/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("/author/edit"))
                .andExpect(model().attribute("author", new AuthorDto(null, null)));
    }

    @DisplayName("страница редактирования автора должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderEditPageWithCorrectViewAndModelAttributes() throws Exception {
        var id = 1L;
        var author = dtoAuthors.stream()
                .filter(it -> it.id().equals(id))
                .findFirst()
                .orElse(null);
        when(authorService.findById(id)).thenReturn(author);
        mvc.perform(get("/authors/%d/edit".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(view().name("/author/edit"))
                .andExpect(model().attribute("author", author));
        verify(authorService, times(1)).findById(id);
    }

    @DisplayName("создание нового автора и перенаправление на контекстный путь")
    @Test
    void shouldNewSaveAuthorAndRedirectToContextPath() throws Exception {
        var expect = new AuthorDto(4L, NEW_AUTHOR_FULL_NAME);
        when(authorService.create(any(AuthorDto.class))).thenReturn(expect);
        mvc.perform(post("/authors").param("fullName", expect.fullName()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/authors/%d".formatted(expect.id())));
        verify(authorService, times(1)).create(any(AuthorDto.class));
    }

    @DisplayName("редактирование автора и перенаправление на контекстный путь")
    @Test
    void shouldSaveAuthorAndRedirectToContextPath() throws Exception {
        var expect = new AuthorDto(3L, NEW_AUTHOR_FULL_NAME);
        mvc.perform(post("/authors")
                        .param("id", expect.id().toString())
                        .param("fullName", expect.fullName())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/authors/%d".formatted(expect.id())));
        verify(authorService, times(1)).update(expect);
    }

    @DisplayName("удаление автора и перенаправление на контекстный путь")
    @Test
    void shouldDeleteAuthorAndRedirectToContextPath() throws Exception {
        var id = 3L;
        mvc.perform(delete("/authors/%d".formatted(id)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/authors"));
        verify(authorService, times(1)).deleteById(id);
    }

    @DisplayName("автор не найдет, перенаправление на страницу с ошибкой")
    @Test
    void shouldRenderErrorPageWhenAuthorNotFound() throws Exception {
        when(authorService.findById(MAX_VALUE)).thenThrow(new AuthorNotFoundException(null));
        mvc.perform(get("/authors/%d".formatted(MAX_VALUE)).locale(ENGLISH))
                .andExpect(status().isNotFound())
                .andExpect(view().name("/error-page"))
                .andExpect(model().attribute("status", NOT_FOUND.value()))
                .andExpect(model().attribute("message", messageSource.getMessage("error.author.not-found", null, ENGLISH)));
        verify(authorService, times(1)).findById(MAX_VALUE);
    }
}
