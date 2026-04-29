package ru.otus.hw.converters;

import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.models.Author;

public class AuthorConverter {

    public static AuthorDto authorDtoOf(Author data) {
        return new AuthorDto(data.getId(), data.getFullName());
    }
}
