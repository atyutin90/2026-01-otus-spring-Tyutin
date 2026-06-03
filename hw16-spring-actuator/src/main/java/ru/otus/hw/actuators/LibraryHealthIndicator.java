package ru.otus.hw.actuators;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import ru.otus.hw.repositories.BookRepository;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;

@Component
@RequiredArgsConstructor
public class LibraryHealthIndicator implements HealthIndicator {

    private final BookRepository bookRepository;

    private final MessageSource messageSource;

    @Override
    public Health health() {
        var all = bookRepository.findAll();
        if (all.isEmpty()) {
            return Health
                .down()
                .withDetail(
                    "info-message",
                    messageSource.getMessage("info.empty-library", null, getLocale())
                ).build();
        }
        return Health
            .up()
            .withDetail(
                "info-message",
                messageSource.getMessage("info.is-not-empty-library", null, getLocale())
            ).build();
    }
}
