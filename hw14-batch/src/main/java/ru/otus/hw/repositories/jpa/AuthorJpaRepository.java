package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.jpa.Author;

import java.util.Optional;

public interface AuthorJpaRepository extends JpaRepository<Author, Long> {

    Optional<Author> findById(long id);
}
