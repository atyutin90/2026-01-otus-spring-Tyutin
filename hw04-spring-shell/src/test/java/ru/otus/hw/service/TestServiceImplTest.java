package ru.otus.hw.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import ru.otus.hw.converter.QuestionConverter;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Student;
import static java.util.List.of;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.mockito.Mockito.*;
import static ru.otus.hw.DataTest.STUB_QUESTION;

@SpringBootTest(classes = {TestServiceImpl.class})
public class TestServiceImplTest {

    private static final String QUESTION_MESSAGE = "\u001B[1m\u25CF\u001B[0m What is 1+1?\n 1) 4\n 2) 3\n 3) 2\n";

    private static final String PROMPT = "TestService.input.correct.option.number";

    private static final String ERROR_MESSAGE = "TestService.incorrect.answer.number";

    private static final String HEADER = "TestService.answer.the.questions";

    private static final int ANSWER_NUMBER = 3;

    private static final int FIRST_NUMBER_ANSWER = 1;

    @Autowired
    private TestService testService;

    @MockitoBean
    private LocalizedIOService mockIoService;

    @MockitoBean
    private QuestionDao mockQuestionDao;

    @MockitoBean
    private QuestionConverter mockQuestionConverter;

    private Student stubStudent;

    @BeforeEach
    void setUp() {
        stubStudent = mock(Student.class);
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
