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
import ru.otus.hw.converters.BookConverter;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.AuthorService;
import ru.otus.hw.services.BookService;
import ru.otus.hw.services.CommentService;
import ru.otus.hw.services.GenreService;

import java.util.List;
import java.util.Set;

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
import static ru.otus.hw.DataTest.NEW_BOOK_TITLE;
import static ru.otus.hw.DataTest.getDbAuthors;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;
import static ru.otus.hw.DataTest.getDbGenres;

@DisplayName("Контроллеры для работы с книгами ")
@WebMvcTest(BookController.class)
@Import({ResourceBundleMessageSource.class})
public class BookControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService bookService;

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private AuthorService authorService;

    @MockitoBean
    private CommentService commentService;

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
        when(genreService.findAll()).thenReturn(dtoGenres);
        when(authorService.findAll()).thenReturn(dtoAuthors);
    }

    @DisplayName("страница со списком книг должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderListPageWithCorrectViewAndModelAttributes() throws Exception {
        when(bookService.findAll()).thenReturn(dtoBooks);
        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("/book/list"))
                .andExpect(model().attribute("books", dtoBooks));
        verify(bookService, times(1)).findAll();
    }

    @DisplayName("страница c просмотром книги должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderShowPageWithCorrectViewAndModelAttributes() throws Exception {
        var id = 1L;
        var book = dtoBooks.stream().filter(it -> it.id().equals(id)).findFirst().orElse(null);
        var dtoComments = dbComments.stream()
                .filter(it -> it.getBook().getId() == id)
                .map(CommentConverter::commentDtoOf)
                .toList();
        when(bookService.findById(id)).thenReturn(book);
        when(commentService.findByBookId(id)).thenReturn(dtoComments);
        mvc.perform(get("/books/%d".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(view().name("/book/show"))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attribute("authors", dtoAuthors))
                .andExpect(model().attribute("genres", dtoGenres))
                .andExpect(model().attribute("comments", dtoComments));
        verify(bookService, times(1)).findById(id);
    }

    @DisplayName("страница для создания новой книги должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderNewEditPageWithCorrectViewAndModelAttributes() throws Exception {
        mvc.perform(get("/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("/book/edit"))
                .andExpect(model().attribute("book", new BookDto(null, null, null, null)))
                .andExpect(model().attribute("authors", dtoAuthors))
                .andExpect(model().attribute("genres", dtoGenres));
    }

    @DisplayName("страница редактирования книги должна отображаться с корректными атрибутами представления и моделью")
    @Test
    void shouldRenderEditPageWithCorrectViewAndModelAttributes() throws Exception {
        var id = 1L;
        var book = dtoBooks.stream()
                .filter(it -> it.id().equals(id))
                .findFirst()
                .orElse(null);
        when(bookService.findById(id)).thenReturn(book);
        mvc.perform(get("/books/%d/edit".formatted(id)))
                .andExpect(status().isOk())
                .andExpect(view().name("/book/edit"))
                .andExpect(model().attribute("book", book))
                .andExpect(model().attribute("authors", dtoAuthors))
                .andExpect(model().attribute("genres", dtoGenres));
        verify(bookService, times(1)).findById(id);
    }

    @DisplayName("создание новой книги и перенаправление на контекстный путь")
    @Test
    void shouldNewSaveBookAndRedirectToContextPath() throws Exception {
        var expect = new BookDto(4L, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));
        when(bookService.create(any(BookDto.class))).thenReturn(expect);
        mvc.perform(post("/books")
                        .param("title", expect.title())
                        .param("author", expect.author().toString())
                        .param("genres", expect.genres().toArray()[0].toString())
                        .param("genres", expect.genres().toArray()[1].toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/books/%d".formatted(expect.id())));
        verify(bookService, times(1)).create(any(BookDto.class));
    }

    @DisplayName("редактирование книги и перенаправление на контекстный путь")
    @Test
    void shouldSaveBookAndRedirectToContextPath() throws Exception {
        var expect = new BookDto(3L, NEW_BOOK_TITLE, 1L, Set.of(1L, 2L));
        mvc.perform(post("/books")
                        .param("id", expect.id().toString())
                        .param("title", expect.title())
                        .param("author", expect.author().toString())
                        .param("genres", expect.genres().toArray()[0].toString())
                        .param("genres", expect.genres().toArray()[1].toString())
                )
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/books/%d".formatted(expect.id())));
        verify(bookService, times(1)).update(expect);
    }

    @DisplayName("удаление книги и перенаправление на контекстный путь")
    @Test
    void shouldDeleteBookAndRedirectToContextPath() throws Exception {
        var id = 3L;
        mvc.perform(delete("/books/%d".formatted(id)))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/books"));
        verify(bookService, times(1)).deleteById(id);
    }

    @DisplayName("книга не найдет, перенаправление на страницу с ошибкой")
    @Test
    void shouldRenderErrorPageWhenAuthorNotFound() throws Exception {
        when(bookService.findById(MAX_VALUE)).thenThrow(new BookNotFoundException(null));
        mvc.perform(get("/books/%d".formatted(MAX_VALUE)).locale(ENGLISH))
                .andExpect(status().isNotFound())
                .andExpect(view().name("/error-page"))
                .andExpect(model().attribute("status", NOT_FOUND.value()))
                .andExpect(model().attribute("message", messageSource.getMessage("error.book.not-found", null, ENGLISH)));
        verify(bookService, times(1)).findById(MAX_VALUE);
    }
}
