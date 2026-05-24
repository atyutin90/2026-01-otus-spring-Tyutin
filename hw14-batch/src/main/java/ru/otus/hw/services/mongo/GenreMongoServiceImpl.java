package ru.otus.hw.services.mongo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.mongo.GenreDoc;
import ru.otus.hw.repositories.mongo.GenreMongoRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreMongoServiceImpl implements GenreMongoService {
    private final GenreMongoRepository genreRepository;

    @Override
    public List<GenreDoc> findAll() {
        return genreRepository.findAll();
    }
}
