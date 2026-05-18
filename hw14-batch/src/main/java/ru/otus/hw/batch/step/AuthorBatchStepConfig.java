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
import ru.otus.hw.cache.AuthorDocCash;
import ru.otus.hw.models.mongo.AuthorDoc;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.repositories.jpa.AuthorJpaRepository;

import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Configuration
@RequiredArgsConstructor
public class AuthorBatchStepConfig {

    private final AuthorJpaRepository authorRepository;

    private final MongoTemplate mongoTemplate;

    private final PlatformTransactionManager platformTransactionManager;

    private final JobRepository jobRepository;

    private final AuthorDocCash cash;

    @Bean
    public Step authorMigration(
        RepositoryItemReader<Author> authorReader,
        ItemProcessor<Author, Pair<Author, AuthorDoc>> authorProcessor,
        ItemMongoWriter<Author, AuthorDoc> authorWriter
    ) {
        return new StepBuilder("authorMigrationStep", jobRepository)
            .<Author, Pair<Author, AuthorDoc>>chunk(5, platformTransactionManager)
            .reader(authorReader)
            .processor(authorProcessor)
            .writer(authorWriter)
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    public RepositoryItemReader<Author> authorReader() {
        return new RepositoryItemReaderBuilder<Author>()
            .name("authorReader")
            .repository(authorRepository)
            .methodName("findAll")
            .pageSize(20)
            .sorts(Map.of("id", ASC))
            .build();
    }

    @Bean
    public ItemProcessor<Author, Pair<Author, AuthorDoc>> authorProcessor() {
        return item -> Pair.of(item, new AuthorDoc(null, item.getFullName()));
    }

    @Bean
    public ItemMongoWriter<Author, AuthorDoc> authorWriter() {
        return new ItemMongoWriter<>(mongoTemplate, cash);
    }
}
