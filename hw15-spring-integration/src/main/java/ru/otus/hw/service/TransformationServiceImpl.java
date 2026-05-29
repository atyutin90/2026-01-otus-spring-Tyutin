package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Butterfly;
import ru.otus.hw.domain.Caterpillar;
import ru.otus.hw.domain.Chrysalis;
import ru.otus.hw.domain.Color;

import java.util.Random;

@Slf4j
@Service
public class TransformationServiceImpl implements TransformationService {

    @Override
    public boolean isChrysalis(Caterpillar caterpillar) {
        return caterpillar.getLength() >= 7;
    }

    @Override
    public boolean isButterfly(Chrysalis chrysalis) {
        return chrysalis.getLength() >= 15;
    }

    @Override
    public Chrysalis toChrysalis(Caterpillar caterpillar) {
        log.info("{} transform Caterpillar -> Chrysalis with parameters: weight: {} gr, length: {} mm, age: {} m",
            caterpillar.getName(), caterpillar.getWeight(), caterpillar.getLength(), caterpillar.getAge()
        );
        return Chrysalis.builder()
            .name(caterpillar.getName())
            .length(caterpillar.getLength())
            .weight(caterpillar.getWeight())
            .age(caterpillar.getAge())
            .build();
    }

    @Override
    public Butterfly toButterfly(Chrysalis chrysalis) {
        log.info("{} transform Chrysalis -> Butterfly with parameters: weight: {} gr, length: {} mm, age: {} m",
            chrysalis.getName(), chrysalis.getWeight(), chrysalis.getLength(), chrysalis.getAge()
        );
        return Butterfly.builder()
            .name(chrysalis.getName())
            .length(chrysalis.getLength())
            .weight(chrysalis.getWeight())
            .age(chrysalis.getAge())
            .color(Color.values()[new Random().nextInt(Color.values().length)])
            .build();
    }
}
