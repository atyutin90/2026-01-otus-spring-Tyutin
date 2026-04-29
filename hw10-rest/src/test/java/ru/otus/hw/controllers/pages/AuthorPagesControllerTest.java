package ru.otus.hw.controllers.pages;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.test.web.servlet.MockMvc;

import static java.util.Locale.ENGLISH;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static ru.otus.hw.DataTest.INVALID_PARAMS;

@DisplayName("Контроллеры для работы с авторами ")
@WebMvcTest(AuthorPagesController.class)
@Import({ResourceBundleMessageSource.class})
public class AuthorPagesControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MessageSource messageSource;

    @DisplayName("страница со списком авторов должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() throws Exception {
        mvc.perform(get("/authors"))
                .andExpect(status().isOk())
                .andExpect(view().name("/author/list"));
    }

    @DisplayName("страница c просмотром автора должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderShowPageWithCorrectViewAndModelAttributes() throws Exception {
        var id = 1L;
        mvc.perform(get("/authors/%d".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(view().name("/author/show"));
    }

    @DisplayName("страница для создания нового автора должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributes() throws Exception {
        mvc.perform(get("/authors/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("/author/show"));
    }

    @DisplayName("не валидный параметр запроса автора, перенаправление на страницу с ошибкой")
    @Test
    void shouldRenderErrorPageWhenInPathInvalidParameter() throws Exception {
        mvc.perform(get("/authors/%s".formatted(INVALID_PARAMS)).locale(ENGLISH))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("/error-page"))
                .andExpect(model().attribute("status", BAD_REQUEST.value()))
                .andExpect(model().attribute("message", messageSource.getMessage("error.bad-request", null, ENGLISH)));
    }
}
