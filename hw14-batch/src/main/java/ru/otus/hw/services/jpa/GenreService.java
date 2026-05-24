package ru.otus.hw.services.jpa;

import ru.otus.hw.models.jpa.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> findAll();
}
