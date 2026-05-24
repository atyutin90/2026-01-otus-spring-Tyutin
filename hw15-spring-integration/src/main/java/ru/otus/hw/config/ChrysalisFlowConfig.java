package ru.otus.hw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import ru.otus.hw.domain.Chrysalis;
import ru.otus.hw.service.FoodService;
import ru.otus.hw.service.TransformationService;

import static ru.otus.hw.config.AppChannelConfig.BUTTERFLY_CHANNEL;
import static ru.otus.hw.config.AppChannelConfig.CHRYSALIS_CHANNEL;

@Configuration
public class ChrysalisFlowConfig {

    @Bean
    public IntegrationFlow chrysalisFlow(FoodService foodService, TransformationService transformationService) {
        return IntegrationFlow.from(CHRYSALIS_CHANNEL)
            .handle(foodService, "eat")
            .<Chrysalis, Boolean>route(
                transformationService::isButterfly,
                mapping -> {
                    mapping.subFlowMapping(false, sf -> sf.channel(CHRYSALIS_CHANNEL));
                    mapping.subFlowMapping(true, sf ->
                        sf.handle(transformationService, "toButterfly")
                            .aggregate()
                            .channel(BUTTERFLY_CHANNEL));
                })
            .get();
    }
}
