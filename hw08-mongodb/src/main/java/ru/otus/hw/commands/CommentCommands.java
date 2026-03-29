package ru.otus.hw.commands;

import lombok.RequiredArgsConstructor;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import ru.otus.hw.converters.CommentConverter;
import ru.otus.hw.models.Comment;
import ru.otus.hw.services.CommentService;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
@ShellComponent
public class CommentCommands {

    private final CommentService commentService;

    private final CommentConverter commentConverter;

    // cbookid 1
    @ShellMethod(value = "Find book comments by book id", key = "cbookid")
    public String findCommentsByBookId(String bookId) {
        return commentService.findByBookId(bookId).stream()
                .map(commentConverter::commentToString)
                .collect(joining("," + lineSeparator()));
    }

    // cbid 1
    @ShellMethod(value = "Find book comment by id", key = "cbid")
    public String findCommentById(String id) {
        return commentService.findById(id)
                .map(commentConverter::commentToString)
                .orElse("Comment with id %s not found".formatted(id));
    }

    // cins good 1
    @ShellMethod(value = "Insert book comment", key = "cins")
    public String insertComment(String text, String bookId) {
        Comment comment = commentService.insert(text, bookId);
        return commentConverter.commentToString(comment);
    }

    // cupd 4 nice 1
    @ShellMethod(value = "Update book comment", key = "cupd")
    public String updateComment(String id, String text, String bookId) {
        Comment comment = commentService.update(id, text, bookId);
        return commentConverter.commentToString(comment);
    }

    // cdel 4
    @ShellMethod(value = "Delete comment by id", key = "cdel")
    public void deleteComment(String id) {
        commentService.deleteById(id);
    }
}
