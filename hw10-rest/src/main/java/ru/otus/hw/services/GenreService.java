package ru.otus.hw.services;

import ru.otus.hw.dto.GenreDto;

import java.util.List;

public interface GenreService {
    List<GenreDto> findAll();

    GenreDto findById(long id);

    GenreDto update(GenreDto data);

    GenreDto create(GenreDto data);

    void deleteById(long id);
}
