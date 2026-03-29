package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.MongoDataTestDataInitializer;
import ru.otus.hw.converters.GenreConverter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Сервисе для работы с жанрами ")
@DataMongoTest
@Import({GenreServiceImpl.class, GenreConverter.class})
public class GenreServiceImplTest extends AbstractServiceTest {

    @Autowired
    private MongoDataTestDataInitializer initializer;

    @Autowired
    private GenreServiceImpl genreService;

    @Autowired
    private GenreConverter genreConverter;

    @BeforeEach
    void setUp() {
        initializer.init();
    }

    @DisplayName("должен загружать список всех жанров без ошибок")
    @Test
    void shouldReturnCorrectAuthorList() {
        assertDoesNotThrow(() -> {
            var authors = genreService.findAll();
            authors.forEach(g -> genreConverter.genreToString(g));
        });
    }
}
