package ru.otus.hw.converter;

import org.springframework.stereotype.Component;
import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import static java.lang.System.lineSeparator;
import static org.apache.commons.lang3.StringUtils.SPACE;

@Component
public class QuestionConverterImpl implements QuestionConverter {

    private static final String BOLD_POINT = "\u001B[1m\u25CF\u001B[0m";

    @Override
    public String convertToString(Question question) {
        var sb = new StringBuilder();
        sb.append(BOLD_POINT)
            .append(SPACE)
            .append(question.text())
            .append(lineSeparator());
        int indexAnswer = 1;
        var answers = question.answers();
        for (Answer answer : answers) {
            sb.append(SPACE)
                .append(indexAnswer++)
                .append(") ")
                .append(answer.text())
                .append(lineSeparator());
        }
        return sb.toString();
    }
}
