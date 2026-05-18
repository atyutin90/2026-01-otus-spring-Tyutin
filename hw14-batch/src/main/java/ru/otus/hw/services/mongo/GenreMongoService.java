package ru.otus.hw.services.mongo;

import ru.otus.hw.models.mongo.GenreDoc;

import java.util.List;

public interface GenreMongoService {
    List<GenreDoc> findAll();
}
