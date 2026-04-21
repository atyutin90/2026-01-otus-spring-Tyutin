package ru.otus.hw.routers.rest.handlers;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.hw.config.RequestParamLocaleContextResolver;
import ru.otus.hw.dto.BookDto;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.services.BookService;

import java.util.function.Function;

import static java.lang.Long.parseLong;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Component
public class BookHandler extends AbstractHandler {

    private final BookService bookService;

    private final MessageSource messageSource;

    public BookHandler(Validator validator,
                       BookService bookService,
                       RequestParamLocaleContextResolver localeResolver,
                       MessageSource messageSource) {
        super(localeResolver, validator);
        this.bookService = bookService;
        this.messageSource = messageSource;
    }


    public Mono<ServerResponse> list(ServerRequest request) {
        return ok().contentType(APPLICATION_JSON).body(bookService.findAll(), GenreDto.class);
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        long id = parseLong(request.pathVariable(ID));
        return bookService.findById(id)
            .flatMap(body -> ok().contentType(APPLICATION_JSON).bodyValue(body))
            .onErrorResume(BookNotFoundException.class, notFoundFunction(request));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(BookDto.class)
            .flatMap(b -> validate(b, request))
            .flatMap(bookService::create)
            .flatMap(body -> status(CREATED).bodyValue(body));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        long id = parseLong(request.pathVariable(ID));
        return request.bodyToMono(BookDto.class)
            .flatMap(b -> validate(b, request))
            .flatMap(a -> bookService.update(a.withId(id)))
            .flatMap(body -> ok().bodyValue(body))
            .onErrorResume(BookNotFoundException.class, notFoundFunction(request));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        long id = parseLong(request.pathVariable(ID));
        return bookService.deleteById(id)
            .then(noContent().build());
    }

    private Function<BookNotFoundException, Mono<? extends ServerResponse>> notFoundFunction(ServerRequest request) {
        return ex -> {
            var errorText = messageSource.getMessage("error.book.not-found", null, getLocale(request));
            return status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .bodyValue(new ErrorMessageDto(errorText));
        };
    }
}
