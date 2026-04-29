package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {
    private final GenreRepository genreRepository;

    @Override
    public List<GenreDto> findAll() {
        return genreRepository.findAll().stream()
                .map(GenreConverter::genreDtoOf)
                .toList();
    }

    @Override
    public GenreDto findById(long id) {
        return genreRepository.findById(id)
                .map(GenreConverter::genreDtoOf)
                .orElseThrow(() -> new GenreNotFoundException("Genre with id: %d not found".formatted(id)));
    }

    @Override
    public GenreDto update(GenreDto data) {
        var id = data.id();
        var genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre with id: %d not found".formatted(id)));
        genre.setName(data.name());
        genre = genreRepository.save(genre);
        return GenreConverter.genreDtoOf(genre);
    }

    @Override
    public GenreDto create(GenreDto data) {
        var genre = genreRepository.save(new Genre(0L, data.name()));
        return GenreConverter.genreDtoOf(genre);
    }

    @Override
    public void deleteById(long id) {
        genreRepository.deleteById(id);
    }
}
