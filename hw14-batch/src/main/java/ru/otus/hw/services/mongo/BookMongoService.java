package ru.otus.hw.services.mongo;

import ru.otus.hw.models.mongo.BookDoc;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface BookMongoService {
    Optional<BookDoc> findById(String id);

    List<BookDoc> findAll();

    BookDoc insert(String title, String authorId, Set<String> genresIds);

    BookDoc update(String id, String title, String authorId, Set<String> genresIds);

    void deleteById(String id);
}
