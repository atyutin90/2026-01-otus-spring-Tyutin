package ru.otus.hw.batch.step;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import ru.otus.hw.cache.AuthorDocCash;
import ru.otus.hw.cache.BookDocCash;
import ru.otus.hw.cache.GenreDocCash;
import ru.otus.hw.models.mongo.AuthorDoc;
import ru.otus.hw.models.mongo.BookDoc;
import ru.otus.hw.models.mongo.CommentDoc;
import ru.otus.hw.models.mongo.GenreDoc;

@Configuration
@RequiredArgsConstructor
public class TaskletStepConfig {

    private final JobRepository jobRepository;

    private final PlatformTransactionManager platformTransactionManager;

    private final MongoTemplate mongoTemplate;

    private final AuthorDocCash authorCash;

    private final GenreDocCash genreCash;

    private final BookDocCash bookDocCash;

    @Bean
    public Step cleanUpStep() {
        return new StepBuilder("cleanUpStep", jobRepository)
            .tasklet(cleanUpTasklet(), platformTransactionManager)
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    public CleanUpTasklet cleanUpTasklet() {
        return new CleanUpTasklet(mongoTemplate);
    }

    @AllArgsConstructor
    public class CleanUpTasklet implements Tasklet {

        private MongoTemplate mongoTemplate;

        @Override
        public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
            mongoTemplate.dropCollection(CommentDoc.class);
            mongoTemplate.dropCollection(BookDoc.class);
            mongoTemplate.dropCollection(GenreDoc.class);
            mongoTemplate.dropCollection(AuthorDoc.class);

            authorCash.clear();
            genreCash.clear();
            bookDocCash.clear();

            return RepeatStatus.FINISHED;
        }
    }
}
