package ru.otus.hw.services.jpa;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.repositories.jpa.AuthorJpaRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorJpaRepository authorJpaRepository;

    @Override
    public List<Author> findAll() {
        return authorJpaRepository.findAll();
    }
}
