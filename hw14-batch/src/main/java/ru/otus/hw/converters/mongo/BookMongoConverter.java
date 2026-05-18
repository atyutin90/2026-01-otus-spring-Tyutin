package ru.otus.hw.converters.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.BookDoc;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class BookMongoConverter {
    private final AuthorMongoConverter authorMongoConverter;

    private final GenreMongoConverter mongoGenreConverter;

    public String bookToString(BookDoc book) {
        var genresString = book.getGenres().stream()
                .map(mongoGenreConverter::genreToString)
                .map("{%s}"::formatted)
                .collect(Collectors.joining(", "));
        return "Id: %s, title: %s, author: {%s}, genres: [%s]".formatted(
                book.getId(),
                book.getTitle(),
                authorMongoConverter.authorToString(book.getAuthor()),
                genresString);
    }
}
