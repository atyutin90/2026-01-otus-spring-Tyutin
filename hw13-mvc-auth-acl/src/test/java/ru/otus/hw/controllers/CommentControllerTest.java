package ru.otus.hw.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.exceptions.CommentNotFoundException;
import ru.otus.hw.services.CommentService;

import java.util.List;

import static java.lang.Long.MAX_VALUE;
import static java.util.Locale.ENGLISH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;

@DisplayName("Контроллеры для работы с комментариями к книге без учета аутентификации")
@WebMvcTest(CommentController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ResourceBundleMessageSource.class})
public class CommentControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private MessageSource messageSource;

    private List<CommentDto> dtoComments;

    @BeforeEach
    void setUp() {
        var dbBooks = getDbBooks();
        dtoComments = getDbComments(dbBooks).stream().map(CommentConverter::commentDtoOf).toList();
    }

    @DisplayName("страница для создания нового комментария должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributes() throws Exception {
        var bookId = 1L;
        mvc.perform(get("/books/%d/comments".formatted(bookId)))
                .andExpect(status().isOk())
                .andExpect(view().name("/comment/edit"))
                .andExpect(model().attribute("comment", new CommentDto(null, null)));
    }

    @DisplayName("страница редактирования комментария должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderEditPageWithCorrectViewAndModelAttributes() throws Exception {
        var id = 1L;
        var bookId = 1L;
        var comment = dtoComments.stream()
                .filter(it -> it.id().equals(id))
                .findFirst()
                .orElse(null);
        when(commentService.findById(id)).thenReturn(comment);
        mvc.perform(get("/books/%d/comments/%d".formatted(bookId, id)))
                .andExpect(status().isOk())
                .andExpect(view().name("/comment/edit"))
                .andExpect(model().attribute("comment", comment));
        verify(commentService, times(1)).findById(id);
    }

    @DisplayName("создание нового комментария и перенаправление на контекстный путь")
    @Test
    void shouldNewSaveAuthorAndRedirectToContextPath() throws Exception {
        var bookId = 1L;
        var expect = new CommentDto(null, NEW_COMMENT_TEXT);
        when(commentService.create(anyLong(), any(CommentDto.class))).thenReturn(expect);
        mvc.perform(post("/books/%d/comments".formatted(bookId))
                        .param("text", expect.text()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/books/%d".formatted(bookId)));
        verify(commentService, times(1)).create(anyLong(), any(CommentDto.class));
    }

    @DisplayName("редактирование комментария и перенаправление на контекстный путь")
    @Test
    void shouldSaveAuthorAndRedirectToContextPath() throws Exception {
        var bookId = 3L;
        var expect = new CommentDto(3L, NEW_COMMENT_TEXT);
        when(commentService.create(anyLong(), any(CommentDto.class))).thenReturn(expect);
        mvc.perform(post("/books/%d/comments".formatted(bookId))
                        .param("id", expect.id().toString())
                        .param("text", expect.text()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/books/%d".formatted(bookId)));
        verify(commentService, times(1)).update(expect);
    }

    @DisplayName("удаление комментария и перенаправление на контекстный путь")
    @Test
    void shouldDeleteAuthorAndRedirectToContextPath() throws Exception {
        var id = 1L;
        var bookId = 1L;
        mvc.perform(delete("/books/%d/comments/%d".formatted(bookId, id)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/books/%d".formatted(bookId)));
        verify(commentService, times(1)).deleteById(id);
    }

    @DisplayName("комментарий не найдет, перенаправление на страницу с ошибкой")
    @Test
    void shouldRenderErrorPageWhenAuthorNotFound() throws Exception {
        var bookId = 1L;
        when(commentService.findById(MAX_VALUE)).thenThrow(new CommentNotFoundException(null));
        mvc.perform(get("/books/%d/comments/%d".formatted(bookId, MAX_VALUE)).locale(ENGLISH))
                .andExpect(status().isNotFound())
                .andExpect(view().name("/error-page"))
                .andExpect(model().attribute("status", NOT_FOUND.value()))
                .andExpect(model().attribute("message", messageSource.getMessage("error.comment.not-found", null, ENGLISH)));
        verify(commentService, times(1)).findById(MAX_VALUE);
    }
}
