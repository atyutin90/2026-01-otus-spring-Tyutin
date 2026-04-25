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
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.services.GenreService;

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
import static ru.otus.hw.DataTest.NEW_GENDER_TEXT;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Контроллеры для работы с жанрами к книге без учета аутентификации")
@WebMvcTest(GenreController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({ResourceBundleMessageSource.class})
public class GenreControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private GenreService genreService;

    @Autowired
    private MessageSource messageSource;

    private List<GenreDto> dtoGenres;

    @BeforeEach
    void setUp() {
        dtoGenres = getDbGenres().stream().map(GenreConverter::genreDtoOf).toList();
    }

    @DisplayName("страница со списком жанров должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() throws Exception {
        when(genreService.findAll()).thenReturn(dtoGenres);
        mvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(view().name("/genre/list"))
                .andExpect(model().attribute("genres", dtoGenres));
        verify(genreService, times(1)).findAll();
    }

    @DisplayName("страница c просмотром жанров должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderShowPageWithCorrectViewAndModelAttributes() throws Exception {
        var id = 1L;
        var genre = dtoGenres.stream().filter(it -> it.id().equals(id)).findFirst().orElse(null);
        when(genreService.findById(id)).thenReturn(genre);
        mvc.perform(get("/genres/%d".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(view().name("/genre/show"))
                .andExpect(model().attribute("genre", genre));
    }

    @DisplayName("страница для создания нового жанра должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributes() throws Exception {
        mvc.perform(get("/genres/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("/genre/edit"))
                .andExpect(model().attribute("genre", new GenreDto(null, null)));
    }

    @DisplayName("страница редактирования жанра должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderEditPageWithCorrectViewAndModelAttributes() throws Exception {
        var id = 1L;
        var genre = dtoGenres.stream()
                .filter(it -> it.id().equals(id))
                .findFirst()
                .orElse(null);
        when(genreService.findById(id)).thenReturn(genre);
        mvc.perform(get("/genres/%d/edit".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(view().name("/genre/edit"))
                .andExpect(model().attribute("genre", genre));
        verify(genreService, times(1)).findById(id);
    }

    @DisplayName("создание нового жанра и перенаправление на контекстный путь")
    @Test
    void shouldNewSaveGenreAndRedirectToContextPath() throws Exception {
        var expect = new GenreDto(7L, NEW_GENDER_TEXT);
        when(genreService.create(any(GenreDto.class))).thenReturn(expect);
        mvc.perform(post("/genres").param("name", expect.name()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/genres/%d".formatted(expect.id())));
        verify(genreService, times(1)).create(any(GenreDto.class));
    }

    @DisplayName("редактирование жанра и перенаправление на контекстный путь")
    @Test
    void shouldSaveGenreAndRedirectToContextPath() throws Exception {
        var expect = new GenreDto(3L, NEW_GENDER_TEXT);
        mvc.perform(post("/genres")
                        .param("id", expect.id().toString())
                        .param("name", expect.name())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/genres/%d".formatted(expect.id())));
        verify(genreService, times(1)).update(expect);
    }

    @DisplayName("удаление жанра и перенаправление на контекстный путь")
    @Test
    void shouldDeleteGenreAndRedirectToContextPath() throws Exception {
        var id = 3L;
        mvc.perform(delete("/genres/%d".formatted(id)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/genres"));
        verify(genreService, times(1)).deleteById(id);
    }

    @DisplayName("жанр не найдет, перенаправление на страницу с ошибкой")
    @Test
    void shouldRenderErrorPageWhenGenreNotFound() throws Exception {
        when(genreService.findById(MAX_VALUE)).thenThrow(new GenreNotFoundException(null));
        mvc.perform(get("/genres/%d".formatted(MAX_VALUE)).locale(ENGLISH))
                .andExpect(status().isNotFound())
                .andExpect(view().name("/error-page"))
                .andExpect(model().attribute("status", NOT_FOUND.value()))
                .andExpect(model().attribute("message", messageSource.getMessage("error.genre.not-found", null, ENGLISH)));
        verify(genreService, times(1)).findById(MAX_VALUE);
    }
}
