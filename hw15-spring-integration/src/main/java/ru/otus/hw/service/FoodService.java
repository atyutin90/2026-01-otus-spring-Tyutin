package ru.otus.hw.service;

import ru.otus.hw.domain.Caterpillar;
import ru.otus.hw.domain.Chrysalis;

public interface FoodService {

    Caterpillar eat(Caterpillar caterpillar);

    Chrysalis eat(Chrysalis chrysalis);

}
