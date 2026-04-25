package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.NEW_AUTHOR_FULL_NAME;
import static ru.otus.hw.DataTest.getDbAuthors;

@DisplayName("Сервис для работы с авторами ")
@DataJpaTest
@Import({AuthorServiceImpl.class})
public class AuthorServiceImplTest {

    @Autowired
    private AuthorService authorService;

    private List<AuthorDto> authorDtoList;

    @BeforeEach
    void setUp() {
        authorDtoList = getDbAuthors().stream().map(AuthorConverter::authorDtoOf).toList();
    }

    @DisplayName("должен загружать автора по id")
    @Test
    void shouldReturnCorrectAuthorById() {
        var id = 1L;
        var expected = authorDtoList.stream()
                .filter(it -> it.id().equals(id))
                .findFirst()
                .orElse(null);
        var dto = authorService.findById(id);
        assertEquals(expected, dto);
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorList() {
        var returedDtoList = authorService.findAll();
        assertEquals(returedDtoList, authorDtoList);
    }

    @DisplayName("должен сохранять нового автора")
    @Test
    void shouldSaveNewAuthor() {
        var expected = new AuthorDto(0L, NEW_AUTHOR_FULL_NAME);
        var returned = authorService.create(expected);
        assertThat(authorService.findById(returned.id())).isEqualTo(expected.withId(returned.id()));
    }

    @DisplayName("должен сохранять измененного автора")
    @Test
    void shouldSaveUpdatedAuthor() {
        var expected = new AuthorDto(1L, NEW_AUTHOR_FULL_NAME);
        assertThat(authorService.findById(expected.id())).isNotEqualTo(expected);
        authorService.update(expected);
        assertThat(authorService.findById(expected.id())).isEqualTo(expected);
    }

    @DisplayName("должен удалять автора по id")
    @Test
    void shouldDeleteById() {
        var id = 3L;
        assertDoesNotThrow(() -> authorService.findById(id));
        authorService.deleteById(id);
        assertThatThrownBy(() -> authorService.findById(id)).isInstanceOf(AuthorNotFoundException.class);
    }
}
