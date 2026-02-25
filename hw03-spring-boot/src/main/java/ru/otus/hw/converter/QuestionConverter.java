package ru.otus.hw.converter;

import ru.otus.hw.domain.Question;

public interface QuestionConverter {
    String convertToString(Question question);
}
