package ru.otus.hw.services.jpa;

import ru.otus.hw.models.jpa.Author;

import java.util.List;

public interface AuthorService {
    List<Author> findAll();
}
