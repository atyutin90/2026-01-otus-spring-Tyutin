package ru.otus.hw.service;

import lombok.RequiredArgsConstructor;
import ru.otus.hw.dao.QuestionDao;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

import static java.lang.System.lineSeparator;
import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import static org.apache.commons.lang3.StringUtils.SPACE;

@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private static final String QUESTIONS_NOT_FOUND_MESSAGE = "Questions not found";

    private final IOService ioService;

    private final QuestionDao questionDao;

    @Override
    public void executeTest() {
        ioService.printLine(EMPTY);
        ioService.printFormattedLine("Please answer the questions below%n");
        var questions = questionDao.findAll();
        if (isNotEmpty(questions)) {
            ioService.printLine(questionsMessage(questions));
        } else {
            ioService.printLine(QUESTIONS_NOT_FOUND_MESSAGE);
        }
    }

    private static String questionsMessage(List<Question> questions) {
        StringBuilder sb = new StringBuilder();
        int indexQuestion = 1;
        for (Question question : questions) {
            sb.append(indexQuestion++)
                .append(". ")
                .append(question.text())
                .append(lineSeparator());
            int indexAnswer = 1;
            var answers = question.answers();
            for (Answer answer : answers) {
                sb
                    .append(SPACE)
                    .append(indexAnswer++)
                    .append(") ")
                    .append(answer.text())
                    .append(lineSeparator());
            }
            sb.append(lineSeparator());
        }
        return sb.toString();
    }
}
