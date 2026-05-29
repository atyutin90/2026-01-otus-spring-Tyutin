package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import ru.otus.hw.domain.Caterpillar;
import ru.otus.hw.service.FoodService;
import ru.otus.hw.service.TransformationService;

import static ru.otus.hw.config.AppChannelConfig.CATERPILLAR_CHANNEL;
import static ru.otus.hw.config.AppChannelConfig.CHRYSALIS_CHANNEL;

@Configuration
public class CaterpillarFlowConfig {

    @Bean
    public IntegrationFlow caterpillarFlow(FoodService foodService, TransformationService transformationService) {
        return IntegrationFlow.from(CATERPILLAR_CHANNEL)
            .handle(foodService, "eat")
            .<Caterpillar, Boolean>route(
                transformationService::isChrysalis,
                mapping -> {
                    mapping.subFlowMapping(false, sf -> sf.channel(CATERPILLAR_CHANNEL));
                    mapping.subFlowMapping(true, sf ->
                        sf.handle(transformationService, "toChrysalis")
                            .channel(CHRYSALIS_CHANNEL));
                })
            .get();
    }
}
