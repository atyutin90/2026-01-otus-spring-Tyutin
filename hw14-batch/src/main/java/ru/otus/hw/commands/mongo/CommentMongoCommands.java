package ru.otus.hw.commands.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.mongo.CommentMongoConverter;
import ru.otus.hw.services.mongo.CommentMongoService;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
@ShellComponent
public class CommentMongoCommands {

    private final CommentMongoService commentMongoService;

    private final CommentMongoConverter mongoCommentConverter;

    // mcbookid 1
    @ShellMethod(value = "Find book comments from Mongo by book id", key = "mcbookid")
    public String findCommentsByBookIdFromMongo(String mongoBookId) {
        return commentMongoService.findByBookId(mongoBookId).stream()
                .map(mongoCommentConverter::commentToString)
                .collect(joining("," + lineSeparator()));
    }
}
