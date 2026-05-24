package ru.otus.hw.service;

import ru.otus.hw.domain.Butterfly;
import ru.otus.hw.domain.Caterpillar;
import ru.otus.hw.domain.Chrysalis;

public interface TransformationService {

    boolean isChrysalis(Caterpillar caterpillar);

    boolean isButterfly(Chrysalis chrysalis);

    Chrysalis toChrysalis(Caterpillar caterpillar);

    Butterfly toButterfly(Chrysalis chrysalis);
}
