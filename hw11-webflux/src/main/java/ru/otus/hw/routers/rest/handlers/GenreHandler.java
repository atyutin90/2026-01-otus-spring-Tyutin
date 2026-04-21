package ru.otus.hw.routers.rest.handlers;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.hw.config.RequestParamLocaleContextResolver;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.services.GenreService;

import java.util.function.Function;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Component
public class GenreHandler extends AbstractHandler {

    private final GenreService genreService;

    private final MessageSource messageSource;

    public GenreHandler(Validator validator,
                        GenreService genreService,
                        RequestParamLocaleContextResolver localeResolver,
                        MessageSource messageSource) {
        super(localeResolver, validator);
        this.genreService = genreService;
        this.messageSource = messageSource;
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        return ok().contentType(APPLICATION_JSON).body(genreService.findAll(), GenreDto.class);
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        long id = Long.parseLong(request.pathVariable("id"));
        return genreService.findById(id)
            .flatMap(body -> ok().contentType(APPLICATION_JSON).bodyValue(body))
            .onErrorResume(GenreNotFoundException.class, notFoundFunction(request));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(GenreDto.class)
            .flatMap(it -> validate(it, request))
            .flatMap(genreService::create)
            .flatMap(body -> status(CREATED).bodyValue(body));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        long id = Long.parseLong(request.pathVariable("id"));
        return request.bodyToMono(GenreDto.class)
            .flatMap(it -> validate(it, request))
            .flatMap(a -> genreService.update(a.withId(id)))
            .flatMap(body -> ok().bodyValue(body))
            .onErrorResume(GenreNotFoundException.class, notFoundFunction(request));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        long id = Long.parseLong(request.pathVariable("id"));
        return genreService.deleteById(id)
            .then(noContent().build());
    }

    private Function<GenreNotFoundException, Mono<? extends ServerResponse>> notFoundFunction(ServerRequest request) {
        return ex -> {
            var errorText = messageSource.getMessage("error.genre.not-found", null, getLocale(request));
            return status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .bodyValue(new ErrorMessageDto(errorText));
        };
    }
}
