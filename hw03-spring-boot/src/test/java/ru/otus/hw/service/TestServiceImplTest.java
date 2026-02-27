package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import ru.otus.hw.converter.QuestionConverter;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Student;
import static java.util.List.of;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.mockito.Mockito.*;
import static ru.otus.hw.DataTest.STUB_QUESTION;

public class TestServiceImplTest {

    private static final String QUESTION_MESSAGE = "\u001B[1m\u25CF\u001B[0m What is 1+1?\n 1) 4\n 2) 3\n 3) 2\n";

    private static final String PROMPT = "TestService.input.correct.option.number";

    private static final String ERROR_MESSAGE = "TestService.incorrect.answer.number";

    private static final String HEADER = "TestService.answer.the.questions";

    private static final int ANSWER_NUMBER = 3;

    private static final int FIRST_NUMBER_ANSWER = 1;

    private TestService testService;

    private LocalizedIOService mockIoService;

    private QuestionDao mockQuestionDao;

    private QuestionConverter mockQuestionConverter;

    private Student stubStudent;

    @BeforeEach
    void setUp() {
        mockIoService = mock(LocalizedIOService.class);
        mockQuestionDao = mock(QuestionDao.class);
        mockQuestionConverter = mock(QuestionConverter.class);
        stubStudent = mock(Student.class);
        testService =  new TestServiceImpl(mockIoService, mockQuestionDao, mockQuestionConverter);
    }

    @Test
    void executeTestSuccessfully() {
        var answers = STUB_QUESTION.answers();
        when(mockQuestionDao.findAll()).thenReturn(of(STUB_QUESTION));
        when(mockQuestionConverter.convertToString(STUB_QUESTION)).thenReturn(QUESTION_MESSAGE);
        when(mockIoService
            .readIntForRangeWithPromptLocalized(FIRST_NUMBER_ANSWER, answers.size(), PROMPT, of(answers.size()), ERROR_MESSAGE)
        ).thenReturn(ANSWER_NUMBER);

        testService.executeTestFor(stubStudent);

        verify(mockQuestionDao).findAll();
        verifyNoMoreInteractions(mockQuestionDao);

        verify(mockQuestionConverter).convertToString(STUB_QUESTION);
        verifyNoMoreInteractions(mockQuestionConverter);

        InOrder inOrder = inOrder(mockIoService);
        inOrder.verify(mockIoService).printLine(EMPTY);
        inOrder.verify(mockIoService).printLineLocalized(HEADER);
        inOrder.verify(mockIoService).printFormattedLine(QUESTION_MESSAGE);
        inOrder.verify(mockIoService)
            .readIntForRangeWithPromptLocalized(FIRST_NUMBER_ANSWER, answers.size(), PROMPT, of(answers.size()), ERROR_MESSAGE);
        inOrder.verifyNoMoreInteractions();
    }
}
