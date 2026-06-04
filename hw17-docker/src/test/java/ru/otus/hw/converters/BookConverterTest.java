package ru.otus.hw.converters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Genre;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.getDbBooks;

public class BookConverterTest {

    @DisplayName("проверка конвертации Book -> BooktDto")
    @Test
    void shouldConvertBookToBookDto() {
        var book = getDbBooks().stream().findFirst().orElse(null);
        var expectedBookDto = new BookDto(book.getId(), book.getTitle(), book.getAuthor().getId(),
                book.getGenres().stream().map(Genre::getId).collect(toSet()));
        assertEquals(BookConverter.bookDtoOf(book), expectedBookDto);
    }
}
