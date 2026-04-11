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
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.services.AuthorService;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.otus.hw.DataTest.NEW_AUTHOR_FULL_NAME;
import static ru.otus.hw.DataTest.getDbAuthors;

@DisplayName("Rest контроллеры для работы с авторами ")
@WebMvcTest(AuthorRestController.class)
@Import({ResourceBundleMessageSource.class})
public class AuthorRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private AuthorService authorService;

    @Autowired
    private MessageSource messageSource;

    private List<AuthorDto> dtoAuthors;

    @BeforeEach
    void setUp() {
        dtoAuthors = getDbAuthors().stream().map(AuthorConverter::authorDtoOf).toList();
    }

    @DisplayName("получение списка авторов")
    @Test
    void shouldReturnCorrectAuthorList() throws Exception {
        when(authorService.findAll()).thenReturn(dtoAuthors);
        mvc.perform(get("/api/authors"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dtoAuthors)));
        verify(authorService, times(1)).findAll();
    }

    @DisplayName("получение автора по заданному идентификатору")
    @Test
    void shouldReturnCorrectAuthorById() throws Exception {
        var id = 1L;
        var author = dtoAuthors.stream().filter(it -> it.id().equals(id)).findFirst().orElse(null);
        when(authorService.findById(id)).thenReturn(author);
        mvc.perform(get("/api/authors/%d".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(author)));
        verify(authorService, times(1)).findById(id);
    }

    @DisplayName("удаление автора по заданному идентификатору")
    @Test
    void shouldDeleteAuthorById() throws Exception {
        var id = 1L;
        mvc.perform(delete("/api/authors/%d".formatted(id)))
                .andExpect(status().isNoContent());
        verify(authorService, times(1)).deleteById(id);
    }

    @DisplayName("создание нового автора")
    @Test
    void shouldAddAuthor() throws Exception {
        var expect = new AuthorDto(null, NEW_AUTHOR_FULL_NAME);
        when(authorService.create(any(AuthorDto.class))).thenReturn(expect);
        mvc.perform(post("/api/authors")
                        .content(mapper.writeValueAsString(expect))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(authorService, times(1)).create(any(AuthorDto.class));
    }

    @DisplayName("редактирование автора по заданному идентификатору")
    @Test
    void shouldUpdateAuthorById() throws Exception {
        var id = 1L;
        var expect = new AuthorDto(id, NEW_AUTHOR_FULL_NAME);
        mvc.perform(patch("/api/authors/%d".formatted(id))
                        .content(mapper.writeValueAsString(expect))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(authorService, times(1)).update(expect);
    }

    @DisplayName("обработка ошибки, что автор не найден при получении жанра по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenGetAuthorByIdNotFound() throws Exception {
        var id = MAX_VALUE;
        var expect = new ErrorMessageDto(messageSource.getMessage("error.author.not-found", null, ENGLISH));
        when(authorService.findById(id)).thenThrow(new AuthorNotFoundException(any()));
        mvc.perform(get("/api/authors/%d".formatted(id)).locale(ENGLISH))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(authorService, times(1)).findById(id);
    }

    @DisplayName("обработка ошибки, что автор не найден при редактировании жанра по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenUpdateAuthorNotFound() throws Exception {
        var id = MAX_VALUE;
        var modify = new AuthorDto(id, NEW_AUTHOR_FULL_NAME);
        var expect = new ErrorMessageDto(messageSource.getMessage("error.author.not-found", null, ENGLISH));
        when(authorService.update(modify)).thenThrow(new AuthorNotFoundException(any()));
        mvc.perform(patch("/api/authors/%d".formatted(id)).locale(ENGLISH)
                        .content(mapper.writeValueAsString(modify))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(authorService, times(1)).update(modify);
    }

    @DisplayName("обработка ошибки валидации при создании автора с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenCreateAuthorWithEmptyRequiredFields() throws Exception {
        var newGenre = new AuthorDto(null, null);
        var expect = Map.of("fullName", "must not be blank");
        mvc.perform(post("/api/authors").locale(ENGLISH)
                        .content(mapper.writeValueAsString(newGenre))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(authorService, times(0)).create(any(AuthorDto.class));
    }

    @DisplayName("обработка ошибки валидации при редактовании автора с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenUpdateAuthorWithEmptyRequiredFields() throws Exception {
        var id = 1L;
        var modify = new AuthorDto(id, null);
        var expect = Map.of("fullName", "must not be blank");
        mvc.perform(patch("/api/authors/%d".formatted(id)).locale(ENGLISH)
                        .content(mapper.writeValueAsString(modify))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(authorService, times(0)).update(any(AuthorDto.class));
    }
}
