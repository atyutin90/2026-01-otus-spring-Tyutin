package ru.otus.hw.dao;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.config.TestFileNameProvider;
import ru.otus.hw.exceptions.QuestionReadException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatList;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static ru.otus.hw.DataTest.STUB_QUESTION;

@SpringBootTest(classes = CsvQuestionDao.class)
public class QuestionDaoTest {

    @MockitoBean
    private TestFileNameProvider mockTestFileNameProvider;

    @Autowired
    private QuestionDao csvQuestionDao;

    @Test
    void shouldLoadAllQuestionsSuccessfully() {
        given(mockTestFileNameProvider.getTestFileName()).willReturn("test.csv");
        var questions = csvQuestionDao.findAll();
        verify(mockTestFileNameProvider, times(1)).getTestFileName();
        assertThatList(questions).isEqualTo(List.of(STUB_QUESTION));
    }

    @Test
    void shouldFailLoadAllQuestions() {
        given(mockTestFileNameProvider.getTestFileName()).willReturn("no_test.csv");
        assertThatThrownBy(() -> csvQuestionDao.findAll()).isInstanceOf(QuestionReadException.class);
    }
}
