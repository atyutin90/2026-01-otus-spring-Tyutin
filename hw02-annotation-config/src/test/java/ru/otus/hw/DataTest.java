package ru.otus.hw;

import ru.otus.hw.domain.Answer;
import ru.otus.hw.domain.Question;

import java.util.List;

public final class DataTest {

    public final static Question STUB_QUESTION = new Question("What is 1+1?", List.of(
        new Answer("4", false),
        new Answer("3", false),
        new Answer("2", true)
    ));
}
