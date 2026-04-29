package ru.otus.hw.converters;

import ru.otus.hw.dto.BookDto;
import ru.otus.hw.models.Book;

import java.util.HashSet;

import static java.util.Collections.EMPTY_SET;
import static org.springframework.util.CollectionUtils.isEmpty;

public class BookConverter {

    public static BookDto bookDtoOf(Book data) {
        return new BookDto(
                data.getId(),
                data.getTitle(),
                data.getAuthorId(),
                isEmpty(data.getGenresIds()) ? EMPTY_SET : new HashSet<>(data.getGenresIds()));
    }
}
