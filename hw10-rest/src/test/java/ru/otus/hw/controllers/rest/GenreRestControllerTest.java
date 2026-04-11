package ru.otus.hw.controllers.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Map;

import static java.lang.Long.MAX_VALUE;
import static java.util.Locale.ENGLISH;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.otus.hw.DataTest.NEW_GENDER_TEXT;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Rest контроллеры для работы с жанрами ")
@WebMvcTest(GenreRestController.class)
@Import({ResourceBundleMessageSource.class})
public class GenreRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private GenreService genreService;

    @Autowired
    private MessageSource messageSource;

    private List<GenreDto> dtoGenres;

    @BeforeEach
    void setUp() {
        dtoGenres = getDbGenres().stream().map(GenreConverter::genreDtoOf).toList();
    }

    @DisplayName("получение списка жанров")
    @Test
    void shouldReturnCorrectGenreList() throws Exception {
        when(genreService.findAll()).thenReturn(dtoGenres);
        mvc.perform(get("/api/genres"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dtoGenres)));
        verify(genreService, times(1)).findAll();
    }

    @DisplayName("получение жанра по заданному идентификатору")
    @Test
    void shouldReturnCorrectGenreById() throws Exception {
        var id = 1L;
        var genre = dtoGenres.stream().filter(it -> it.id().equals(id)).findFirst().orElse(null);
        when(genreService.findById(id)).thenReturn(genre);
        mvc.perform(get("/api/genres/%d".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(genre)));
        verify(genreService, times(1)).findById(id);
    }

    @DisplayName("удаление жанра по заданному идентификатору")
    @Test
    void shouldDeleteGenreById() throws Exception {
        var id = 1L;
        mvc.perform(delete("/api/genres/%d".formatted(id)))
                .andExpect(status().isNoContent());
        verify(genreService, times(1)).deleteById(id);
    }

    @DisplayName("создание нового жанра")
    @Test
    void shouldAddGenre() throws Exception {
        var expect = new GenreDto(null, NEW_GENDER_TEXT);
        when(genreService.create(any(GenreDto.class))).thenReturn(expect);
        mvc.perform(post("/api/genres")
                        .content(mapper.writeValueAsString(expect))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(genreService, times(1)).create(any(GenreDto.class));
    }

    @DisplayName("редактирование жанра по заданному идентификатору")
    @Test
    void shouldUpdateGenreById() throws Exception {
        var id = 1L;
        var expect = new GenreDto(id, NEW_GENDER_TEXT);
        mvc.perform(patch("/api/genres/%d".formatted(id))
                        .content(mapper.writeValueAsString(expect))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(genreService, times(1)).update(expect);
    }

    @DisplayName("обработка ошибки, что жанр не найден при получении жанра по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenGetGenreByIdNotFound() throws Exception {
        var id = MAX_VALUE;
        var expect = new ErrorMessageDto(messageSource.getMessage("error.genre.not-found", null, ENGLISH));
        when(genreService.findById(id)).thenThrow(new GenreNotFoundException(any()));
        mvc.perform(get("/api/genres/%d".formatted(id)).locale(ENGLISH))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(genreService, times(1)).findById(id);
    }

    @DisplayName("обработка ошибки, что жанр не найден при редактировании жанра по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenUpdateGenreNotFound() throws Exception {
        var id = MAX_VALUE;
        var modify = new GenreDto(id, NEW_GENDER_TEXT);
        var expect = new ErrorMessageDto(messageSource.getMessage("error.genre.not-found", null, ENGLISH));
        when(genreService.update(modify)).thenThrow(new GenreNotFoundException(any()));
        mvc.perform(patch("/api/genres/%d".formatted(id)).locale(ENGLISH)
                        .content(mapper.writeValueAsString(modify))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(genreService, times(1)).update(modify);
    }

    @DisplayName("обработка ошибки валидации при создании жанра с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenCreateGenreWithEmptyRequiredFields() throws Exception {
        var newGenre = new GenreDto(null, null);
        var expect = Map.of("name", "must not be blank");
        mvc.perform(post("/api/genres").locale(ENGLISH)
                        .content(mapper.writeValueAsString(newGenre))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(genreService, times(0)).create(any(GenreDto.class));
    }

    @DisplayName("обработка ошибки валидации при редактировании жанра с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenUpdateGenreWithEmptyRequiredFields() throws Exception {
        var id = 1L;
        var modify = new GenreDto(id, null);
        var expect = Map.of("name", "must not be blank");
        mvc.perform(patch("/api/genres/%d".formatted(id)).locale(ENGLISH)
                        .content(mapper.writeValueAsString(modify))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(genreService, times(0)).update(any(GenreDto.class));
    }
}
