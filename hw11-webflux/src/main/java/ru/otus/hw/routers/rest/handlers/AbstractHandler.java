package ru.otus.hw.routers.rest.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.i18n.LocaleContextResolver;
import reactor.core.publisher.Mono;

import java.util.Locale;

@RequiredArgsConstructor
public abstract class AbstractHandler {

    protected static final String ID = "id";

    protected static final String BOOK_ID = "bookId";

    private final LocaleContextResolver localeResolver;

    private final Validator validator;

    protected <T> Mono<T> validate(T body, ServerRequest request) {
        return Mono.deferContextual(contextView -> {

            // Устанавливаем локаль в контекст
            LocaleContextHolder.setLocale(getLocale(request));

            BindingResult errors = new BeanPropertyBindingResult(body, body.getClass().getName());
            validator.validate(body, errors);

            if (errors.hasErrors()) {
                return Mono.error(new WebExchangeBindException(null, errors));
            }
            return Mono.just(body);
        });
    }

    public Locale getLocale(ServerRequest request) {
        return localeResolver.resolveLocaleContext(request.exchange()).getLocale();
    }
}
