package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import reactor.test.StepVerifier;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.BookNotFoundException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD;
import static ru.otus.hw.DataTest.NEW_AUTHOR_FULL_NAME;
import static ru.otus.hw.DataTest.getDbAuthors;

@DisplayName("Сервис для работы с авторами ")
@DataR2dbcTest
@Import({AuthorServiceImpl.class})
@DirtiesContext(classMode = AFTER_EACH_TEST_METHOD)
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

        StepVerifier
            .create(authorService.findById(id))
            .assertNext(it -> assertThat(expected).isEqualTo(it))
            .verifyComplete();
    }

    @DisplayName("должен загружать список всех авторов")
    @Test
    void shouldReturnCorrectAuthorList() {
        StepVerifier
            .create(authorService.findAll().collectList())
            .assertNext(authors -> assertThat(authorDtoList).containsExactlyInAnyOrderElementsOf(authors))
            .verifyComplete();
    }

    @DisplayName("должен сохранять нового автора")
    @Test
    void shouldSaveNewAuthor() {
        var expected = new AuthorDto(0L, NEW_AUTHOR_FULL_NAME);
        StepVerifier
            .create(authorService.create(expected))
            .assertNext(it -> {
                assertThat(it).isNotNull();
                assertThat(it.id()).isGreaterThan(0);
                assertThat(it.fullName()).isEqualTo(expected.fullName());
            }).verifyComplete();
    }

    @DisplayName("должен сохранять измененного автора")
    @Test
    void shouldSaveUpdatedAuthor() {
        var expected = new AuthorDto(1L, NEW_AUTHOR_FULL_NAME);
        StepVerifier
            .create(authorService.update(expected))
            .assertNext(it -> {
                assertThat(it).isNotNull();
                assertThat(it.id()).isEqualTo(expected.id());
                assertThat(it.fullName()).isEqualTo(expected.fullName());
            }).verifyComplete();
    }

    @DisplayName("должен удалять автора по id")
    @Test
    void shouldDeleteById() {
        var id = 3L;
        StepVerifier
            .create(authorService.deleteById(id).then(authorService.findById(id)))
            .consumeErrorWith(throwable -> assertThat(throwable).isInstanceOf(BookNotFoundException.class));
    }
}
