package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.AuthorConverter;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.models.Author;
import ru.otus.hw.repositories.AuthorRepository;

import java.util.List;

import static org.springframework.security.acls.domain.BasePermission.DELETE;
import static org.springframework.security.acls.domain.BasePermission.WRITE;

@RequiredArgsConstructor
@Service
public class AuthorServiceImpl implements AuthorService {
    private final AuthorRepository authorRepository;

    private final AclServiceService aclServiceService;

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

    @PreAuthorize("hasPermission(#data.id, 'ru.otus.hw.models.Author', 'WRITE')")
    @Transactional
    @Override
    public void update(AuthorDto data) {
        var id = data.id();
        var author = authorRepository.findById(id)
                .orElseThrow(() -> new AuthorNotFoundException("Author with id: %d not found".formatted(id)));
        author.setFullName(data.fullName());
        authorRepository.save(author);
    }

    @Secured({"ROLE_ADMIN"})
    @Transactional
    @Override
    public AuthorDto create(AuthorDto data) {
        var author = authorRepository.save(new Author(0L, data.fullName()));
        aclServiceService.createAcl(author, true, WRITE, DELETE);
        return AuthorConverter.authorDtoOf(author);
    }

    @PreAuthorize("hasPermission(#id, 'ru.otus.hw.models.Author', 'DELETE')")
    @Transactional
    @Override
    public void deleteById(long id) {
        aclServiceService.deleteAcl(id, Author.class);
        authorRepository.deleteById(id);
    }
}
