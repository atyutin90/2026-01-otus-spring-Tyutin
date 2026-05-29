package ru.otus.hw.gateway;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.otus.hw.config.AppChannelConfig;
import ru.otus.hw.config.CaterpillarFlowConfig;
import ru.otus.hw.config.ChrysalisFlowConfig;
import ru.otus.hw.config.FileReaderFlowConfig;
import ru.otus.hw.domain.Butterfly;
import ru.otus.hw.domain.Caterpillar;
import ru.otus.hw.domain.Chrysalis;
import ru.otus.hw.service.FileService;
import ru.otus.hw.service.FoodService;
import ru.otus.hw.service.TransformationService;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.domain.Color.BLUE;
import static ru.otus.hw.domain.Color.RED;

@SpringJUnitConfig({
    AppChannelConfig.class,
    CaterpillarFlowConfig.class,
    ChrysalisFlowConfig.class,
    FileReaderFlowConfig.class,
    FileGateway.class
})
@EnableIntegration
@IntegrationComponentScan(basePackageClasses = FileGateway.class)
@SpringIntegrationTest
public class FireGatewayTest {

    @Autowired
    private FileGateway fileGateway;

    @MockitoBean
    private FileService fileService;

    @MockitoBean
    private FoodService foodService;

    @MockitoBean
    private TransformationService transformationService;

    Caterpillar beforeBobCaterpillar;
    Caterpillar afterBobCaterpillar;

    Caterpillar beforeAlexCaterpillar;
    Caterpillar afterAlexCaterpillar;

    Chrysalis beforeBobChrysalis;
    Chrysalis afterBobChrysalis;

    Chrysalis beforeAlexChrysalis;
    Chrysalis afterAlexChrysalis;

    Butterfly bobButterfly;

    Butterfly alexButterfly;

    @BeforeEach
    public void init() {
        beforeBobCaterpillar = Caterpillar.builder().name("Bob").age(0).length(0).weight(0).build();
        afterBobCaterpillar = Caterpillar.builder().name("Bob").age(8).length(8).weight(8).build();

        beforeAlexCaterpillar = Caterpillar.builder().name("Alex").age(0).length(0).weight(0).build();
        afterAlexCaterpillar = Caterpillar.builder().name("Alex").age(8).length(8).weight(8).build();

        beforeBobChrysalis = Chrysalis.builder().name("Bob").age(8).length(8).weight(8).build();
        afterBobChrysalis = Chrysalis.builder().name("Bob").age(15).length(15).weight(15).build();

        beforeAlexChrysalis = Chrysalis.builder().name("Alex").age(8).length(8).weight(8).build();
        afterAlexChrysalis = Chrysalis.builder().name("Alex").age(15).length(15).weight(15).build();

        bobButterfly = Butterfly.builder().name("Bob").age(15).length(15).weight(15).color(BLUE).build();
        alexButterfly = Butterfly.builder().name("Alex").age(15).length(15).weight(15).color(RED).build();
    }

    @Test
    public void activePilotTest() {
        Mockito.when(fileService.getCaterpillarsData((Mockito.any(String.class))))
            .thenReturn(List.of(beforeBobCaterpillar, beforeAlexCaterpillar));

        Mockito.when(foodService.eat(beforeBobCaterpillar))
            .thenReturn(afterBobCaterpillar);

        Mockito.when(foodService.eat(beforeAlexCaterpillar))
            .thenReturn(afterAlexCaterpillar);

        Mockito.when(foodService.eat(beforeBobChrysalis))
            .thenReturn(afterBobChrysalis);

        Mockito.when(foodService.eat(beforeAlexChrysalis))
            .thenReturn(afterAlexChrysalis);

        Mockito.when(transformationService.isChrysalis(Mockito.any(Caterpillar.class)))
            .thenReturn(true);

        Mockito.when(transformationService.isButterfly(Mockito.any(Chrysalis.class)))
            .thenReturn(true);

        Mockito.when(transformationService.toChrysalis(afterBobCaterpillar))
            .thenReturn(beforeBobChrysalis);

        Mockito.when(transformationService.toChrysalis(afterAlexCaterpillar))
            .thenReturn(beforeAlexChrysalis);

        Mockito.when(transformationService.toButterfly(afterBobChrysalis))
            .thenReturn(bobButterfly);

        Mockito.when(transformationService.toButterfly(afterAlexChrysalis))
            .thenReturn(alexButterfly);

        Collection<Butterfly> result = fileGateway.process("test.txt");
        assertThat(result).isNotEmpty();
        assertThat(result.size()).isEqualTo(2);
        assertThat(result.containsAll(List.of(bobButterfly, alexButterfly)));
    }
}
