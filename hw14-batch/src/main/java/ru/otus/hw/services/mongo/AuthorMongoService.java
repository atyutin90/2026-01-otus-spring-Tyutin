package ru.otus.hw.services.mongo;

import ru.otus.hw.models.mongo.AuthorDoc;

import java.util.List;

public interface AuthorMongoService {

    List<AuthorDoc> findAll();
}
