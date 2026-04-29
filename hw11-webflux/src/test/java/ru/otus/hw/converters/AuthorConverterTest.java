package ru.otus.hw.converters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dto.AuthorDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.getDbAuthors;

public class AuthorConverterTest {

    @DisplayName("проверка конвертации Author -> AuthorDto")
    @Test
    void shouldConvertAuthorToAuthorDto() {
        var author = getDbAuthors().stream().findFirst().orElse(null);
        var expectedDtoAuthor = new AuthorDto(author.getId(), author.getFullName());
        assertEquals(AuthorConverter.authorDtoOf(author), expectedDtoAuthor);
    }
}
