package ru.otus.hw.converters.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.CommentDoc;

@RequiredArgsConstructor
@Component
public class CommentMongoConverter {
    private final BookMongoConverter bookConverter;

    public String commentToString(CommentDoc comment) {
        return "Id: %s, text: %s, book: {Id: %s}".formatted(
                comment.getId(),
                comment.getText(),
                comment.getBook().getId());
    }
}
