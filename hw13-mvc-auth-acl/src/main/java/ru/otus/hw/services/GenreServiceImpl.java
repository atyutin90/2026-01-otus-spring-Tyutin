package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.models.Genre;
import ru.otus.hw.repositories.GenreRepository;

import java.util.List;

import static org.springframework.security.acls.domain.BasePermission.DELETE;
import static org.springframework.security.acls.domain.BasePermission.WRITE;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;

    private final AclServiceService aclServiceService;

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

    @PreAuthorize("hasPermission(#data.id, 'ru.otus.hw.models.Genre', 'WRITE')")
    @Transactional
    @Override
    public void update(GenreDto data) {
        var id = data.id();
        var genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre with id: %d not found".formatted(id)));
        genre.setName(data.name());
        genreRepository.save(genre);
    }

    @Secured({"ROLE_ADMIN"})
    @Transactional
    @Override
    public GenreDto create(GenreDto data) {
        var genre = new Genre(0L, data.name());
        genre = genreRepository.save(genre);
        aclServiceService.createAcl(genre, true, WRITE, DELETE);
        return GenreConverter.genreDtoOf(genre);
    }

    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Genre', 'DELETE')")
    @Transactional
    @Override
    public void deleteById(long id) {
        aclServiceService.deleteAcl(id, Genre.class);
        genreRepository.deleteById(id);
    }
}
