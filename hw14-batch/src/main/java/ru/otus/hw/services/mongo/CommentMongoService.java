package ru.otus.hw.services.mongo;

import ru.otus.hw.models.mongo.CommentDoc;

import java.util.List;
import java.util.Optional;

public interface CommentMongoService {
    Optional<CommentDoc> findById(String id);

    List<CommentDoc> findByBookId(String bookId);

    CommentDoc insert(String text, String bookId);

    CommentDoc update(String id, String text, String bookId);

    void deleteById(String id);
}
