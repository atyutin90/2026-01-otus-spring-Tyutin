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
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.services.BookService;

import java.util.List;
import java.util.Map;
import java.util.Set;

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
import static ru.otus.hw.DataTest.NEW_BOOK_TITLE;
import static ru.otus.hw.DataTest.NEW_GENDER_TEXT;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Rest контроллеры для работы с книгами ")
@WebMvcTest(BookRestController.class)
@Import({ResourceBundleMessageSource.class})
public class BookRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockitoBean
    private BookService bookService;

    @Autowired
    private MessageSource messageSource;

    private List<BookDto> dtoBooks;

    @BeforeEach
    void setUp() {
        var dbBooks = getDbBooks();
        dtoBooks = dbBooks.stream().map(BookConverter::bookDtoOf).toList();
    }

    @DisplayName("получение списка книг")
    @Test
    void shouldReturnCorrectBookList() throws Exception {
        when(bookService.findAll()).thenReturn(dtoBooks);
        mvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(dtoBooks)));
        verify(bookService, times(1)).findAll();
    }

    @DisplayName("получение книги по заданному идентификатору")
    @Test
    void shouldReturnCorrectBookById() throws Exception {
        var id = 1L;
        var book = dtoBooks.stream().filter(it -> it.id().equals(id)).findFirst().orElse(null);
        when(bookService.findById(id)).thenReturn(book);
        mvc.perform(get("/api/books/%d".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(book)));
        verify(bookService, times(1)).findById(id);
    }

    @DisplayName("удаление книги по заданному идентификатору")
    @Test
    void shouldDeleteBookById() throws Exception {
        var id = 1L;
        mvc.perform(delete("/api/books/%d".formatted(id)))
                .andExpect(status().isNoContent());
        verify(bookService, times(1)).deleteById(id);
    }

    @DisplayName("создание новой книги")
    @Test
    void shouldAddBook() throws Exception {
        var expect = new BookDto(null, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));
        when(bookService.create(any(BookDto.class))).thenReturn(expect);
        mvc.perform(post("/api/books")
                        .content(mapper.writeValueAsString(expect))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isCreated());
        verify(bookService, times(1)).create(any(BookDto.class));
    }

    @DisplayName("редактирование книги по заданному идентификатору")
    @Test
    void shouldUpdateBookById() throws Exception {
        var id = 1L;
        var expect = new BookDto(id, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));
        mvc.perform(patch("/api/books/%d".formatted(id))
                        .content(mapper.writeValueAsString(expect))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(bookService, times(1)).update(expect);
    }

    @DisplayName("обработка ошибки, что книга не найден при получении книги по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenGetBookByIdNotFound() throws Exception {
        var id = MAX_VALUE;
        var expect = new ErrorMessageDto(messageSource.getMessage("error.book.not-found", null, ENGLISH));
        when(bookService.findById(id)).thenThrow(new BookNotFoundException(any()));
        mvc.perform(get("/api/books/%d".formatted(id)).locale(ENGLISH))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(bookService, times(1)).findById(id);
    }

    @DisplayName("обработка ошибки, что книга не найден при редактировании книги по несуществующему идентификатору")
    @Test
    void shouldReturn404WithErrorMessageWhenUpdateBookNotFound() throws Exception {
        var id = MAX_VALUE;
        var modify = new BookDto(id, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));
        var expect = new ErrorMessageDto(messageSource.getMessage("error.book.not-found", null, ENGLISH));
        when(bookService.update(modify)).thenThrow(new BookNotFoundException(any()));
        mvc.perform(patch("/api/books/%d".formatted(id)).locale(ENGLISH)
                        .content(mapper.writeValueAsString(modify))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(bookService, times(1)).update(modify);
    }

    @DisplayName("обработка ошибки валидации при создании книги с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenCreateBookWithEmptyRequiredFields() throws Exception {
        var newGenre = new BookDto(null, null, null, null);
        var expect = Map.of(
                "title", "must not be blank",
                "authorId", "must not be blank",
                "genreIds", "must not be blank"
        );
        mvc.perform(post("/api/books").locale(ENGLISH)
                        .content(mapper.writeValueAsString(newGenre))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(bookService, times(0)).create(any(BookDto.class));
    }

    @DisplayName("обработка ошибки валидации при редактировании книги с незаполнеными обязательными полями")
    @Test
    void shouldReturn400WithErrorMessageWhenUpdateBookWithEmptyRequiredFields() throws Exception {
        var id = 1L;
        var modify =  new BookDto(id, null, null, null);
        var expect = Map.of(
                "title", "must not be blank",
                "authorId", "must not be blank",
                "genreIds", "must not be blank"
        );
        mvc.perform(patch("/api/books/%d".formatted(id)).locale(ENGLISH)
                        .content(mapper.writeValueAsString(modify))
                        .contentType(APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().json(mapper.writeValueAsString(expect)));
        verify(bookService, times(0)).update(any(BookDto.class));
    }
}
