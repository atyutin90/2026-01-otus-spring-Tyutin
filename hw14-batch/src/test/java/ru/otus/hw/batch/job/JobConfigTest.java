package ru.otus.hw.batch.job;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.otus.hw.models.jpa.Author;
import ru.otus.hw.models.jpa.Genre;
import ru.otus.hw.models.mongo.AuthorDoc;
import ru.otus.hw.models.mongo.GenreDoc;
import ru.otus.hw.repositories.mongo.AuthorMongoRepository;
import ru.otus.hw.repositories.mongo.BookMongoRepository;
import ru.otus.hw.repositories.mongo.CommentMongoRepository;
import ru.otus.hw.repositories.mongo.GenreMongoRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.batch.core.BatchStatus.COMPLETED;
import static ru.otus.hw.DataTest.getDbAuthors;
import static ru.otus.hw.DataTest.getDbBooks;
import static ru.otus.hw.DataTest.getDbComments;
import static ru.otus.hw.DataTest.getDbGenres;
import static ru.otus.hw.batch.job.JobConfig.MIGRATION_JOB;

@SpringBootTest
@SpringBatchTest
public class JobConfigTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private AuthorMongoRepository authorMongoRepository;

    @Autowired
    private GenreMongoRepository genreMongoRepository;

    @Autowired
    private BookMongoRepository bookMongoRepository;

    @Autowired
    private CommentMongoRepository commentMongoRepository;

    @BeforeEach
    void clearMetaData() {
        jobRepositoryTestUtils.removeJobExecutions();
    }

    @Test
    void testJob() throws Exception {
        Job job = jobLauncherTestUtils.getJob();

        assertNotNull(job);

        assertEquals(MIGRATION_JOB, job.getName());

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(new JobParameters());

        assertEquals(COMPLETED, jobExecution.getStatus());

        var dbAuthors = getDbAuthors();
        var dbGenres = getDbGenres();
        var dbBooks = getDbBooks();
        var dbComments = getDbComments(dbBooks);

        var authorDocs = authorMongoRepository.findAll();
        var genreDocs = genreMongoRepository.findAll();
        var bookDocs = bookMongoRepository.findAll();
        var commentDocs = commentMongoRepository.findAll();

        //Проверка авторов
        assertThat(authorDocs).isNotEmpty();
        assertEquals(
            dbAuthors.stream().map(Author::getFullName).toList(),
            authorDocs.stream().map(AuthorDoc::getFullName).toList()
        );

        //Проверка жанров
        assertThat(genreDocs).isNotEmpty();
        assertEquals(
            dbGenres.stream().map(Genre::getName).toList(),
            genreDocs.stream().map(GenreDoc::getName).toList()
        );

        //Проверка книг
        assertThat(bookDocs).isNotEmpty();
        dbBooks.forEach(book -> {
            var doc = bookDocs.stream().filter(it -> it.getTitle().equals(book.getTitle())).findFirst().orElse(null);
            assertNotNull(doc);
            assertEquals(book.getTitle(), doc.getTitle());
            assertEquals(book.getAuthor().getFullName(), doc.getAuthor().getFullName());
            assertEquals(book.getGenres().stream().map(Genre::getName).toList(), doc.getGenres().stream().map(GenreDoc::getName).toList());
        });

        //Проверка комментариев
        assertThat(commentDocs).isNotEmpty();
        dbComments.forEach(comment -> {
            var doc = commentDocs.stream().filter(it -> it.getText().equals(comment.getText())).findFirst().orElse(null);
            assertNotNull(doc);
            assertEquals(comment.getBook().getTitle(), doc.getBook().getTitle());
        });
    }

}
