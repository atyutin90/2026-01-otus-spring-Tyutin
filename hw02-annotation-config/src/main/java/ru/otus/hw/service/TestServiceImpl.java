package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converter.QuestionConverter;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import static java.lang.String.format;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final int FIRST_POSITION_QUESTION = 1;

    private static final String ANSI_RESET = "\u001B[0m";

    private static final String ANSI_RED = "\u001B[31m";

    private static final String ANSI_BLUE = "\u001B[34m";

    private final IOService ioService;

    private final QuestionDao questionDao;

    private final QuestionConverter questionConverter;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        var testResult = new TestResult(student);
        for (Question question : questions) {
            var isAnswerValid = askQuestion(question);
            testResult.applyAnswer(question, isAnswerValid);
        }
        return testResult;
    }

    private boolean askQuestion(Question question) {
        var questionMessage = questionConverter.convertToString(question);
        ioService.printFormattedLine(questionMessage);
        var answers = question.answers();
        int studentAnswerNumber = ioService.readIntForRangeWithPrompt(
            FIRST_POSITION_QUESTION,
            answers.size(),
            format("%sPlease input correct option number (1 to %s):%s", ANSI_BLUE, answers.size(), ANSI_RESET),
            format("%sIncorrect answer number%s", ANSI_RED, ANSI_RESET)
        );
        var index = studentAnswerNumber - 1;
        return index < answers.size() && answers.get(index).isCorrect();
    }
}
