package ru.otus.hw.runner;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import ru.otus.hw.config.AppProperties;
import ru.otus.hw.domain.Butterfly;
import ru.otus.hw.gateway.FileGateway;

import java.util.Collection;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@AllArgsConstructor
@Service
public class AppRunner implements ApplicationRunner {

    private final AppProperties appProperties;

    private final FileGateway fileGateway;

    @Override
    public void run(ApplicationArguments args) {
        Collection<Butterfly> butterflies = fileGateway.process(appProperties.getFile());
        log.info("\r\nButterfly collection:\r\n{}", butterflies.stream()
            .map(b -> format("Name: %s, Weight: %d gr, Length: %d mm, Age: %d m, Color: %s",
                b.getName(),  b.getWeight(), b.getLength(), b.getAge(), b.getColor())
            ).collect(Collectors.joining("\r\n"))
        );
    }
}
