package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import ru.otus.hw.converter.QuestionConverter;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Student;

import java.util.List;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.otus.hw.DataTest.STUB_QUESTION;

public class TestServiceImplTest {

    private static final String QUESTION_MESSAGE = "\u001B[1m\u25CF\u001B[0m What is 1+1?\n 1) 4\n 2) 3\n 3) 2\n";

    private static final String PROMPT = "\u001B[34mPlease input correct option number (1 to %s):\u001B[0m";

    private static final String ERROR_MESSAGE = "\u001B[31mIncorrect answer number\u001B[0m";

    private static final String HEADER = "Please answer the questions below%n";

    private static final int ANSWER_NUMBER = 3;

    private static final int FIRST_NUMBER_ANSWER = 1;

    private TestService testService;

    private IOService mockIoService;

    private QuestionDao mockQuestionDao;

    private QuestionConverter mockQuestionConverter;

    private Student stubStudent;


    @BeforeEach
    void setUp() {
        mockIoService = mock(IOService.class);
        mockQuestionDao = mock(QuestionDao.class);
        mockQuestionConverter = mock(QuestionConverter.class);
        stubStudent = mock(Student.class);
        testService =  new TestServiceImpl(mockIoService, mockQuestionDao, mockQuestionConverter);
    }

    @Test
    void executeTestSuccessfully() {
        var answers = STUB_QUESTION.answers();
        when(mockQuestionDao.findAll()).thenReturn(List.of(STUB_QUESTION));
        when(mockQuestionConverter.convertToString(STUB_QUESTION)).thenReturn(QUESTION_MESSAGE);
        when(mockIoService
            .readIntForRangeWithPrompt(1, answers.size(), format(PROMPT, answers.size()), ERROR_MESSAGE)
        ).thenReturn(ANSWER_NUMBER);

        testService.executeTestFor(stubStudent);

        verify(mockQuestionDao).findAll();
        verifyNoMoreInteractions(mockQuestionDao);

        verify(mockQuestionConverter).convertToString(STUB_QUESTION);
        verifyNoMoreInteractions(mockQuestionConverter);

        InOrder inOrder = inOrder(mockIoService);
        inOrder.verify(mockIoService).printLine(EMPTY);
        inOrder.verify(mockIoService).printFormattedLine(HEADER);
        inOrder.verify(mockIoService).printFormattedLine(QUESTION_MESSAGE);
        inOrder.verify(mockIoService)
            .readIntForRangeWithPrompt(1, answers.size(), format(PROMPT, answers.size()), ERROR_MESSAGE);
        inOrder.verifyNoMoreInteractions();
    }
}
