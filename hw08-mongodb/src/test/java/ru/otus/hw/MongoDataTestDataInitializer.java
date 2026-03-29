package ru.otus.hw;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import static ru.otus.hw.DataTest.getDbAuthors;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;
import static ru.otus.hw.DataTest.getDbGenres;

@Component
@RequiredArgsConstructor
public class MongoDataTestDataInitializer {

    private final MongoTemplate mongoTemplate;

    public void init() {
        mongoTemplate.getMongoDatabaseFactory().getMongoDatabase().drop();
        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        var dbBooks = getDbBooks(dbAuthors, dbGenres);
        var dbComments = getDbComments(dbBooks);

        mongoTemplate.insert(dbAuthors, "authors");
        mongoTemplate.insert(dbGenres, "genres");
        mongoTemplate.insert(dbBooks, "books");
        mongoTemplate.insert(dbComments, "comments");
    }
}
