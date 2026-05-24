package ru.otus.hw.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.otus.hw.models.mongo.GenreDoc;

import java.util.List;
import java.util.Set;

public interface GenreMongoRepository extends MongoRepository<GenreDoc, String> {

    List<GenreDoc> findByIdIsIn(Set<String> ids);
}
