package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    @Override
    public List<AuthorDto> findAll() {
        return authorRepository.findAll().stream()
                .map(AuthorConverter::authorDtoOf)
                .toList();
    }

    @Override
    public AuthorDto findById(long id) {
        return authorRepository.findById(id)
                .map(AuthorConverter::authorDtoOf)
                .orElseThrow(() -> new AuthorNotFoundException("Author with id: %d not found".formatted(id)));
    }

    @Transactional
    @Override
    public AuthorDto update(AuthorDto data) {
        var id = data.id();
        var author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author with id: %d not found".formatted(id)));
        author.setFullName(data.fullName());
        author = authorRepository.save(author);
        return AuthorConverter.authorDtoOf(author);
    }

    @Transactional
    @Override
    public AuthorDto create(AuthorDto data) {
        var author = authorRepository.save(new Author(0L, data.fullName()));
        return AuthorConverter.authorDtoOf(author);
    }

    @Transactional
    @Override
    public void deleteById(long id) {
        authorRepository.deleteById(id);
    }
}
