package ru.otus.hw.converters.mongo;

import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.GenreDoc;

@Component
public class GenreMongoConverter {
    public String genreToString(GenreDoc genre) {
        return "Id: %s, Name: %s".formatted(genre.getId(), genre.getName());
    }
}
