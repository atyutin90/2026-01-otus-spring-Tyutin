package ru.otus.hw.batch.step;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.MongoItemWriterBuilder;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.cache.BookDocCash;
import ru.otus.hw.models.mongo.CommentDoc;
import ru.otus.hw.models.jpa.Comment;
import ru.otus.hw.repositories.jpa.CommentJpaRepository;

import java.util.Map;

import static org.springframework.data.domain.Sort.Direction.ASC;

@Configuration
@RequiredArgsConstructor
public class CommentBatchStepConfig {

    private final CommentJpaRepository commentRepository;

    private final MongoTemplate mongoTemplate;

    private final PlatformTransactionManager platformTransactionManager;

    private final JobRepository jobRepository;

    private final BookDocCash bookCash;

    @Bean
    public Step commentMigration(
        RepositoryItemReader<Comment> commentReader,
        ItemProcessor<Comment, CommentDoc> commentProcessor,
        MongoItemWriter<CommentDoc> commentWriter
    ) {
        return new StepBuilder("commentMigrationStep", jobRepository)
            .<Comment, CommentDoc>chunk(5, platformTransactionManager)
            .reader(commentReader)
            .processor(commentProcessor)
            .writer(commentWriter)
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    public RepositoryItemReader<Comment> commentReader() {
        return new RepositoryItemReaderBuilder<Comment>()
            .name("commentReader")
            .repository(commentRepository)
            .methodName("findAll")
            .pageSize(20)
            .sorts(Map.of("id", ASC))
            .build();
    }

    @Bean
    public ItemProcessor<Comment, CommentDoc> commentProcessor() {
        return item -> {
            var bookId = item.getBook() != null ? item.getBook().getId() : null;
            var bookDoc = bookId != null ? bookCash.get(bookId) : null;
            return new CommentDoc(null, item.getText(), bookDoc);
        };
    }

    @Bean
    public MongoItemWriter<CommentDoc> commentWriter() {
        return new MongoItemWriterBuilder<CommentDoc>()
            .collection("comments")
            .template(mongoTemplate)
            .build();
    }
}
