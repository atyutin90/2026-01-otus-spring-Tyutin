package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.otus.hw.converter.QuestionConverter;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Question;
import ru.otus.hw.domain.Student;
import ru.otus.hw.domain.TestResult;

import static java.util.List.of;

@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final int FIRST_POSITION_QUESTION = 1;

    private static final String PROMPT_CODE = "TestService.input.correct.option.number";

    private static final String ERROR_MESSAGE_CODE = "TestService.incorrect.answer.number";

    private final LocalizedIOService ioService;

    private final QuestionDao questionDao;

    private final QuestionConverter questionConverter;

    @Override
    public TestResult executeTestFor(Student student) {
        ioService.printLine("");
        ioService.printLineLocalized("TestService.answer.the.questions");
        ioService.printLine("");

        var questions = questionDao.findAll();
        var testResult = new TestResult(student);

        for (var question: questions) {
            var isAnswerValid = askQuestion(question);
            testResult.applyAnswer(question, isAnswerValid);
        }

        return testResult;
    }

    private boolean askQuestion(Question question) {
        var questionMessage = questionConverter.convertToString(question);
        ioService.printFormattedLine(questionMessage);
        var answers = question.answers();
        int studentAnswerNumber = ioService.readIntForRangeWithPromptLocalized(
                FIRST_POSITION_QUESTION,
                answers.size(),
                PROMPT_CODE,
                of(answers.size()),
                ERROR_MESSAGE_CODE
        );
        var index = studentAnswerNumber - 1;
        return index < answers.size() && answers.get(index).isCorrect();
    }

}
