package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.CommentDoc;

import java.util.List;

public interface CommentMongoRepository extends MongoRepository<CommentDoc, String> {

    void deleteByBookId(String id);

    List<CommentDoc> findByBookId(String bookId);
}
