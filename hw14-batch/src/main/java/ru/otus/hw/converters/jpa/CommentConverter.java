package ru.otus.hw.converters.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.jpa.Comment;

@RequiredArgsConstructor
@Component
public class CommentConverter {

    public String commentToString(Comment comment) {
        return "Id: %d, text: %s, book: {Id: %d}".formatted(
                comment.getId(),
                comment.getText(),
                comment.getBook().getId());
    }
}
