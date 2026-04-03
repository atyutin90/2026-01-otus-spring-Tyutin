package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.MongoDataTestDataInitializer;
import ru.otus.hw.converters.AuthorConverter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Сервисе для работы с авторами ")
@DataMongoTest
@Import({AuthorServiceImpl.class, AuthorConverter.class})
public class AuthorServiceImplTest extends AbstractServiceTest {

    @Autowired
    private MongoDataTestDataInitializer initializer;

    @Autowired
    private AuthorServiceImpl authorService;

    @Autowired
    private AuthorConverter authorConverter;

    @BeforeEach
    void setUp() {
        initializer.init();
    }

    @DisplayName("должен загружать список всех авторов без ошибок")
    @Test
    void shouldReturnCorrectAuthorList() {
        assertDoesNotThrow(() -> {
            var authors = authorService.findAll();
            authors.forEach(a -> authorConverter.authorToString(a));
        });
    }
}
