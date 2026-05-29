package ru.otus.hw.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Butterfly {
    private String name;

    private Integer weight;

    private Integer length;

    private Integer age;

    private Color color;
}
