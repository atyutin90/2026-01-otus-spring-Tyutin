package ru.otus.hw.dao;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.RequiredArgsConstructor;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.dao.dto.QuestionDto;
import ru.otus.hw.domain.Question;
import ru.otus.hw.exceptions.QuestionReadException;

import java.io.IOException;
import java.io.InputStreamReader;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.util.List;

import static ru.otus.hw.utils.FileUtils.getResourceInputStream;

@RequiredArgsConstructor
public class CsvQuestionDao implements QuestionDao {

    private static final int SKIP_LINES = 1;

    private static final char COMMA = ';';

    private static final String QUESTION_READ_ERROR_MESSAGE = "Error reading data from file: %s";

    private final TestFileNameProvider fileNameProvider;

    @Override
    public List<Question> findAll() {
        List<Question> questions;
        try (var inputFile = new InputStreamReader(getResourceInputStream(fileNameProvider.getTestFileName()), UTF_8)) {
            var questionDtoList = new CsvToBeanBuilder<QuestionDto>(inputFile)
                .withType(QuestionDto.class)
                .withSkipLines(SKIP_LINES)
                .withSeparator(COMMA)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();

            questions = questionDtoList.stream()
                .map(QuestionDto::toDomainObject)
                .toList();
        } catch (IOException e) {
            throw new QuestionReadException(format(QUESTION_READ_ERROR_MESSAGE, fileNameProvider.getTestFileName()), e);
        }
        return questions;
    }
}
