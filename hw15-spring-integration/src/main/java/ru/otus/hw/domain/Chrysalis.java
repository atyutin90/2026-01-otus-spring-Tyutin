package ru.otus.hw.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.With;

@Data
@Builder
@AllArgsConstructor
@With
public class Chrysalis {
    private String name;

    private Integer weight;

    private Integer length;

    private Integer age;
}
