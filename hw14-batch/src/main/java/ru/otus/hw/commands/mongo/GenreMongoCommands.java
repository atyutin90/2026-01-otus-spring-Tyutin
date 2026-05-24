package ru.otus.hw.commands.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.mongo.GenreMongoConverter;
import ru.otus.hw.services.mongo.GenreMongoService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class GenreMongoCommands {

    private final GenreMongoService genreService;

    private final GenreMongoConverter genreConverter;

    // mag
    @ShellMethod(value = "Find all genres from Mongo", key = "mag")
    public String findAllGenresFromMongo() {
        return genreService.findAll().stream()
                .map(genreConverter::genreToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }
}
