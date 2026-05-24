package ru.otus.hw.commands.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.mongo.BookMongoConverter;
import ru.otus.hw.services.mongo.BookMongoService;

import java.util.stream.Collectors;

@RequiredArgsConstructor
@ShellComponent
public class BookMongoCommands {

    private final BookMongoService bookMongoService;

    private final BookMongoConverter bookConverter;

    // mab
    @ShellMethod(value = "Find all books from Mongo", key = "mab")
    public String findAllBooksFromMongo() {
        return bookMongoService.findAll().stream()
                .map(bookConverter::bookToString)
                .collect(Collectors.joining("," + System.lineSeparator()));
    }
}
