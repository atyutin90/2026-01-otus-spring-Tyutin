package ru.otus.hw.commands.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.mongo.AuthorMongoConverter;
import ru.otus.hw.services.mongo.AuthorMongoService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class AuthorMongoCommands {

    private final AuthorMongoService authorMongoService;

    private final AuthorMongoConverter authorMongoConverter;

    // maa
    @ShellMethod(value = "Find all authors from Mongo", key = "maa")
    public String findAllAuthorsFromMongo() {
        return authorMongoService.findAll().stream()
                .map(authorMongoConverter::authorToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }
}
