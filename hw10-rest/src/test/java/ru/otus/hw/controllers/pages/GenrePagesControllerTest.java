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

@DisplayName("Контроллеры для работы с жанрами ")
@WebMvcTest(GenrePagesController.class)
@Import({ResourceBundleMessageSource.class})
public class GenrePagesControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private MessageSource messageSource;

    @DisplayName("страница со списком жанров должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() throws Exception {
        mvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(view().name("/genre/list"));
    }

    @DisplayName("страница c просмотром жанров должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderShowPageWithCorrectViewAndModelAttributes() throws Exception {
        var id = 1L;
        mvc.perform(get("/genres/%d".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(view().name("/genre/show"));
    }

    @DisplayName("страница для создания нового жанра должна отображаться с корректными атрибутами представления")
    @Test
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributes() throws Exception {
        mvc.perform(get("/genres/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("/genre/show"));
    }

    @DisplayName("не валидный параметр запроса жанра, перенаправление на страницу с ошибкой")
    @Test
    void shouldRenderErrorPageWhenInPathInvalidParameter() throws Exception {
        mvc.perform(get("/genres/%s".formatted(INVALID_PARAMS)).locale(ENGLISH))
                .andExpect(status().isBadRequest())
                .andExpect(view().name("/error-page"))
                .andExpect(model().attribute("status", BAD_REQUEST.value()))
                .andExpect(model().attribute("message", messageSource.getMessage("error.bad-request", null, ENGLISH)));
    }
}
