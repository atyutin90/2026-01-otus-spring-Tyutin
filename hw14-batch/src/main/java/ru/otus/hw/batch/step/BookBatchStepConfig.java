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
import ru.otus.hw.cache.BookDocCash;
import ru.otus.hw.cache.GenreDocCash;
import ru.otus.hw.models.mongo.BookDoc;
import ru.otus.hw.models.mongo.GenreDoc;
import ru.otus.hw.models.jpa.Book;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.repositories.jpa.BookJpaRepository;

import java.util.ArrayList;
import java.util.Map;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.springframework.data.domain.Sort.Direction.ASC;

@Configuration
@RequiredArgsConstructor
public class BookBatchStepConfig {

    private final BookJpaRepository bookRepository;

    private final MongoTemplate mongoTemplate;

    private final PlatformTransactionManager platformTransactionManager;

    private final JobRepository jobRepository;

    private final AuthorDocCash authorCash;

    private final BookDocCash bookCash;

    private final GenreDocCash genreCash;

    @Bean
    public Step bookMigration(
        RepositoryItemReader<Book> bookReader,
        ItemProcessor<Book, Pair<Book, BookDoc>> bookProcessor,
        ItemMongoWriter<Book, BookDoc> bookWriter
    ) {
        return new StepBuilder("bookMigrationStep", jobRepository)
            .<Book, Pair<Book, BookDoc>>chunk(5, platformTransactionManager)
            .reader(bookReader)
            .processor(bookProcessor)
            .writer(bookWriter)
            .allowStartIfComplete(true)
            .build();
    }

    @Bean
    public RepositoryItemReader<Book> bookReader() {
        return new RepositoryItemReaderBuilder<Book>()
            .name("bookReader")
            .repository(bookRepository)
            .methodName("findAll")
            .pageSize(20)
            .sorts(Map.of("id", ASC))
            .build();
    }

    @Bean
    public ItemProcessor<Book, Pair<Book, BookDoc>> bookProcessor() {
        return item -> {
            var authorId = item.getAuthor() != null ? item.getAuthor().getId() : null;
            var genreIds = item.getGenres() != null ?
                item.getGenres().stream().map(Genre::getId).toList() :
                new ArrayList<Long>();
            var authorDoc = authorId != null ? authorCash.get(authorId) : null;
            var genreDocs = isNotEmpty(genreIds) ? genreCash.get(genreIds) : new ArrayList<GenreDoc>();
            return Pair.of(item, new BookDoc(null, item.getTitle(), authorDoc, genreDocs));
        };
    }

    @Bean
    public ItemMongoWriter<Book, BookDoc> bookWriter() {
        return new ItemMongoWriter<>(mongoTemplate, bookCash);
    }
}
