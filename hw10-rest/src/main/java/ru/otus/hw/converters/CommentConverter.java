package ru.otus.hw.converters;

import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.models.Comment;

public class CommentConverter {

    public static CommentDto commentDtoOf(Comment comment) {
        return new CommentDto(comment.getId(), comment.getBook().getId(), comment.getText());
    }
}
