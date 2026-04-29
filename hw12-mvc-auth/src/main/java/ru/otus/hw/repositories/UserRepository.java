package ru.otus.hw.repositories;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.models.User;

import java.util.Optional;

import static ru.otus.hw.models.User.USER_GRAPH;

public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(value = USER_GRAPH)
    Optional<User> findByUsername(String username);
}
