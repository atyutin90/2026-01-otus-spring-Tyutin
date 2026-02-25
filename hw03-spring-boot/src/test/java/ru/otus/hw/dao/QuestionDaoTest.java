package ru.otus.hw.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static ru.otus.hw.DataTest.STUB_QUESTION;

public class QuestionDaoTest {

    private TestFileNameProvider mockTestFileNameProvider;

    private QuestionDao mockCsvQuestionDao;

    @BeforeEach
    void setUp() {
        mockTestFileNameProvider = mock(TestFileNameProvider.class);
        mockCsvQuestionDao = new CsvQuestionDao(mockTestFileNameProvider);
    }

    @Test
    void shouldLoadAllQuestionsSuccessfully() {
        given(mockTestFileNameProvider.getTestFileName()).willReturn("test.csv");
        var questions = mockCsvQuestionDao.findAll();
        verify(mockTestFileNameProvider, times(1)).getTestFileName();
        assertThatList(questions).isEqualTo(List.of(STUB_QUESTION));
    }

    @Test
    void shouldFailLoadAllQuestions() {
        given(mockTestFileNameProvider.getTestFileName()).willReturn("no_test.csv");
        assertThatThrownBy(() -> mockCsvQuestionDao.findAll()).isInstanceOf(QuestionReadException.class);
    }
}
