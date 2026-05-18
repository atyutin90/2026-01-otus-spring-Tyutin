package ru.otus.hw;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import ru.otus.hw.repositories.jpa.AuthorJpaRepository;
import ru.otus.hw.repositories.jpa.BookJpaRepository;
import ru.otus.hw.repositories.jpa.CommentJpaRepository;
import ru.otus.hw.repositories.jpa.GenreJpaRepository;

/**
 * Так как @EnableMongoRepositories активен в @DataJpaTest, поэтому делаем отдельную конфигурацию.
 */
@Configuration
@EnableJpaRepositories(basePackageClasses = {AuthorJpaRepository.class, BookJpaRepository.class, GenreJpaRepository.class, CommentJpaRepository.class})
@EntityScan(basePackages = "ru.otus.hw.models.jpa")
public class JpaConfig {
}
