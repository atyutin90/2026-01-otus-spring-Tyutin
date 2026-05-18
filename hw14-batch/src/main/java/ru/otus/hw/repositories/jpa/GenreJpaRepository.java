package ru.otus.hw.repositories.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.jpa.Genre;

import java.util.List;
import java.util.Set;

public interface GenreJpaRepository extends JpaRepository<Genre, Long> {

    List<Genre> findByIdIsIn(Set<Long> ids);
}
