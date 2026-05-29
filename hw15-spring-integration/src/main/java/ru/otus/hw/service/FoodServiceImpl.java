package ru.otus.hw.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Caterpillar;
import ru.otus.hw.domain.Chrysalis;

import java.util.Random;

import static java.lang.Thread.sleep;

@Slf4j
@Service
public class FoodServiceImpl implements FoodService {

    @Override
    public Caterpillar eat(Caterpillar caterpillar) {
        log.info("Caterpillar parameters before eating: name: {}, weight: {} gr, length: {} mm, age: {} m",
            caterpillar.getName(), caterpillar.getWeight(), caterpillar.getLength(), caterpillar.getAge()
        );

        Random random = new Random();
        int itemWeight = random.nextInt(1, 4);
        int itemLength = itemWeight % 2;
        int itemAge = random.nextInt(0, 2);
        caterpillar.setLength(caterpillar.getLength() + itemLength);
        caterpillar.setWeight(caterpillar.getWeight() + itemWeight);
        caterpillar.setAge(caterpillar.getAge() + itemAge);
        delay();
        log.info("Caterpillar parameters after eating: name: {}, weight: {} gr, length: {} mm, age: {} m",
            caterpillar.getName(), caterpillar.getWeight(), caterpillar.getLength(), caterpillar.getAge()
        );
        return caterpillar;
    }

    @Override
    public Chrysalis eat(Chrysalis chrysalis) {
        log.info("Chrysalis parameters before eating: name: {}, weight: {} gr, length: {} mm, age: {} m",
            chrysalis.getName(), chrysalis.getWeight(), chrysalis.getLength(), chrysalis.getAge()
        );

        Random random = new Random();
        int itemWeight = random.nextInt(1, 4);
        int itemLength = itemWeight % 2;
        int itemAge = random.nextInt(0, 2);
        chrysalis.setLength(chrysalis.getLength() + itemLength);
        chrysalis.setWeight(chrysalis.getWeight() + itemWeight);
        chrysalis.setAge(chrysalis.getAge() + itemAge);
        delay();
        log.info("Chrysalis parameters after eating: name: {}, weight: {} gr, length: {} mm, age: {} m",
            chrysalis.getName(), chrysalis.getWeight(), chrysalis.getLength(), chrysalis.getAge()
        );
        return chrysalis;
    }

    private static void delay() {
        try {
            sleep(100);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
