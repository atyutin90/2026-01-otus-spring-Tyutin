package ru.otus.hw.repositories;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import ru.otus.hw.models.Book;

import java.util.List;
import java.util.Optional;

import static ru.otus.hw.models.Book.BOOK_GRAPH;

@RepositoryRestResource(path = "book")
public interface BookRepository extends JpaRepository<Book, Long> {

    @EntityGraph(value = BOOK_GRAPH)
    Optional<Book> findById(long id);

    @Nonnull
    @EntityGraph(value = BOOK_GRAPH)
    List<Book> findAll();
}
