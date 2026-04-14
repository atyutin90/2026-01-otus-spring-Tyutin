package ru.otus.hw.converters;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;
import ru.otus.hw.models.Genre;

import static java.util.Collections.EMPTY_SET;
import static java.util.stream.Collectors.toSet;
import static org.springframework.util.CollectionUtils.isEmpty;

public class BookConverter {

    public static BookDto bookDtoOf(Book data) {
        return new BookDto(
                data.getId(),
                data.getTitle(),
                data.getAuthor() != null ? data.getAuthor().getId() : null,
                isEmpty(data.getGenres()) ? EMPTY_SET : data.getGenres().stream().map(Genre::getId).collect(toSet()));
    }
}
