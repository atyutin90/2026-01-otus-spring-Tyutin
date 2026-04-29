package ru.otus.hw.routers.rest.handlers;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.hw.config.RequestParamLocaleContextResolver;
import ru.otus.hw.dto.AuthorDto;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.exceptions.AuthorNotFoundException;
import ru.otus.hw.services.AuthorService;

import java.util.function.Function;

import static java.lang.Long.parseLong;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Component
public class AuthorHandler extends AbstractHandler {

    private final AuthorService authorService;

    private final MessageSource messageSource;

    public AuthorHandler(Validator validator,
                         AuthorService authorService,
                         RequestParamLocaleContextResolver localeResolver,
                         MessageSource messageSource) {
        super(localeResolver, validator);
        this.authorService = authorService;
        this.messageSource = messageSource;
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        return ok().contentType(APPLICATION_JSON).body(authorService.findAll(), AuthorDto.class);
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        long id = parseLong(request.pathVariable(ID));
        return authorService.findById(id)
            .flatMap(body -> ok().contentType(APPLICATION_JSON).bodyValue(body))
            .onErrorResume(AuthorNotFoundException.class, notFoundFunction(request));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(AuthorDto.class)
            .flatMap(a -> validate(a, request))
            .flatMap(authorService::create)
            .flatMap(body -> status(CREATED).bodyValue(body));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        long id = parseLong(request.pathVariable(ID));
        return request.bodyToMono(AuthorDto.class)
            .flatMap(a -> validate(a, request))
            .flatMap(a -> authorService.update(a.withId(id)))
            .flatMap(body -> ok().bodyValue(body))
            .onErrorResume(AuthorNotFoundException.class, notFoundFunction(request));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        long id = parseLong(request.pathVariable(ID));
        return authorService.deleteById(id)
            .then(noContent().build());
    }

    private Function<AuthorNotFoundException, Mono<? extends ServerResponse>> notFoundFunction(ServerRequest request) {
        return ex -> {
            var errorText = messageSource.getMessage("error.author.not-found", null, getLocale(request));
            return status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .bodyValue(new ErrorMessageDto(errorText));
        };
    }
}
