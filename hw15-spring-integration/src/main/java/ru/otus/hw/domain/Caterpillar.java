package ru.otus.hw.domain;

import com.opencsv.bean.CsvBindByPosition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.With;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@With
public class Caterpillar {

    @CsvBindByPosition(position = 0)
    private String name;

    @CsvBindByPosition(position = 1)
    private Integer weight;

    @CsvBindByPosition(position = 2)
    private Integer length;

    @CsvBindByPosition(position = 3)
    private Integer age;

}
