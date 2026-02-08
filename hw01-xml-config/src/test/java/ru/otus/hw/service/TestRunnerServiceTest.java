package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.Collections;
import java.util.List;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TestRunnerServiceTest {

    @Mock
    private QuestionDao questionDao;

    @Mock
    private IOService ioService;

    private TestRunnerService testRunnerService;

    private static final String HEADER = "Please answer the questions below%n";

    private static final List<Answer> stubAnswers = List.of(
        new Answer("4", false),
        new Answer("3", false),
        new Answer("2", true)
    );

    private static final Question stubQuestion = new Question("What is 1+1?", stubAnswers);

    private static final List<Question> stubQuestions = List.of(stubQuestion);

    private static final String EXPECTED_QUESTIONS_OUTPUT = """
            1. What is 1+1?
             1) 4
             2) 3
             3) 2

            """;

    private static final String QUESTIONS_NOT_FOUND = "Questions not found";

    @BeforeEach
    void setUp() {
        TestService testService = new TestServiceImpl(ioService, questionDao);
        testRunnerService = new TestRunnerServiceImpl(testService);
    }

    @Test
    void shouldPrintQuestions() {
        when(questionDao.findAll()).thenReturn(stubQuestions);
        testRunnerService.run();

        verify(questionDao).findAll();
        verifyNoMoreInteractions(questionDao);

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printLine(EMPTY);
        inOrder.verify(ioService).printFormattedLine(HEADER);
        inOrder.verify(ioService).printLine(EXPECTED_QUESTIONS_OUTPUT);
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    void shouldPrintMessageThatQuestionsNotFound() {
        when(questionDao.findAll()).thenReturn(Collections.emptyList());
        testRunnerService.run();

        verify(questionDao).findAll();
        verifyNoMoreInteractions(questionDao);

        InOrder inOrder = inOrder(ioService);
        inOrder.verify(ioService).printLine(EMPTY);
        inOrder.verify(ioService).printFormattedLine(HEADER);
        inOrder.verify(ioService).printLine(QUESTIONS_NOT_FOUND);
        inOrder.verifyNoMoreInteractions();
    }
}
