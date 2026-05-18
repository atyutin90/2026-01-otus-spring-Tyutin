package ru.otus.hw.batch.step;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.batch.writer.ItemMongoWriter;
import ru.otus.hw.cache.GenreDocCash;
import ru.otus.hw.models.mongo.GenreDoc;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.repositories.jpa.GenreJpaRepository;

import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Configuration
@RequiredArgsConstructor
public class GenreBatchStepConfig {

    private final GenreJpaRepository genreRepository;

    private final MongoTemplate mongoTemplate;

    private final PlatformTransactionManager platformTransactionManager;

    private final JobRepository jobRepository;

    private final GenreDocCash cash;

    @Bean
    public Step genreMigration(
        RepositoryItemReader<Genre> genreReader,
        ItemProcessor<Genre, Pair<Genre, GenreDoc>> genreProcessor,
        ItemMongoWriter<Genre, GenreDoc> genreWriter
    ) {
        return new StepBuilder("genreMigrationStep", jobRepository)
            .<Genre, Pair<Genre, GenreDoc>>chunk(5, platformTransactionManager)
            .reader(genreReader)
            .processor(genreProcessor)
            .writer(genreWriter)
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    public RepositoryItemReader<Genre> genreReader() {
        return new RepositoryItemReaderBuilder<Genre>()
            .name("genreReader")
            .repository(genreRepository)
            .methodName("findAll")
            .pageSize(20)
            .sorts(Map.of("id", ASC))
            .build();
    }

    @Bean
    public ItemProcessor<Genre, Pair<Genre, GenreDoc>> genreProcessor() {
        return item -> Pair.of(item, new GenreDoc(null, item.getName()));
    }

    @Bean
    public ItemMongoWriter<Genre, GenreDoc> genreWriter() {
        return new ItemMongoWriter<>(mongoTemplate, cash);
    }
}
