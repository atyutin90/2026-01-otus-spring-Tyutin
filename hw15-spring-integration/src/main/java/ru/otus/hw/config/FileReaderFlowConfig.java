package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

import ru.otus.hw.service.FileService;

import java.io.IOException;

import static ru.otus.hw.config.AppChannelConfig.CATERPILLAR_CHANNEL;
import static ru.otus.hw.config.AppChannelConfig.FILE_CHANNEL;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class FileReaderFlowConfig {

    @Bean
    public IntegrationFlow fileReadingFlow(FileService fileService) throws IOException {
        return IntegrationFlow.from(FILE_CHANNEL)
            .handle(fileService, "getCaterpillarsData")
            .split()
            .transform(source -> {
                return source;
            })
            .channel(CATERPILLAR_CHANNEL)
            .get();
    }
}
