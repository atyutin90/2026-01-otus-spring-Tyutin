package ru.otus.hw.config;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AppProperties implements TestConfig, TestFileNameProvider {
    private int rightAnswersCountToPass;

    private String testFileName;
}
