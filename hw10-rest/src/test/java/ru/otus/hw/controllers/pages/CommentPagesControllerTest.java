package ru.otus.hw.controllers.pages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.otus.hw.services.CommentService;

import static java.util.Locale.ENGLISH;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static ru.otus.hw.DataTest.INVALID_PARAMS;

@DisplayName("Контроллеры для работы с комментариями к книге ")
@WebMvcTest(CommentPagesController.class)
@Import({ResourceBundleMessageSource.class})
public class CommentPagesControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private CommentService commentService;

    @Autowired
    private MessageSource messageSource;

    @DisplayName("страница для создания нового комментария должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributes() throws Exception {
        var bookId = 1L;
        mvc.perform(get("/books/%d/comments".formatted(bookId)))
                .andExpect(status().isOk())
                .andExpect(view().name("/comment/show"));
    }

    @DisplayName("страница редактирования комментария должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderEditPageWithCorrectViewAndModelAttributes() throws Exception {
        var id = 1L;
        var bookId = 1L;
        mvc.perform(get("/books/%d/comments/%d".formatted(bookId, id)))
                .andExpect(status().isOk())
                .andExpect(view().name("/comment/show"));
    }

    @DisplayName("не валидный параметр запроса комментария, перенаправление на страницу с ошибкой")
    @Test
    void shouldRenderErrorPageWhenInPathInvalidParameter() throws Exception {
        var bookId = 1L;
        mvc.perform(get("/books/%d/comments/%s".formatted(bookId, INVALID_PARAMS)).locale(ENGLISH))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("/error-page"))
                .andExpect(model().attribute("status", BAD_REQUEST.value()))
                .andExpect(model().attribute("message", messageSource.getMessage("error.bad-request", null, ENGLISH)));
    }
}
