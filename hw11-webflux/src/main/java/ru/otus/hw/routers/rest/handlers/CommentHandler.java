package ru.otus.hw.routers.rest.handlers;

import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import ru.otus.hw.config.RequestParamLocaleContextResolver;
import ru.otus.hw.dto.CommentDto;
import ru.otus.hw.dto.ErrorMessageDto;
import ru.otus.hw.exceptions.BookNotFoundException;
import ru.otus.hw.exceptions.CommentNotFoundException;
import ru.otus.hw.services.CommentService;

import java.util.function.Function;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.server.ServerResponse.status;

@Component
public class CommentHandler extends AbstractHandler {

    private final CommentService commentService;

    private final MessageSource messageSource;

    public CommentHandler(Validator validator,
                          CommentService commentService,
                          RequestParamLocaleContextResolver localeResolver,
                          MessageSource messageSource) {
        super(localeResolver, validator);
        this.commentService = commentService;
        this.messageSource = messageSource;
    }

    public Mono<ServerResponse> list(ServerRequest request) {
        long bookId = Long.parseLong(request.pathVariable(BOOK_ID));
        return ok().contentType(APPLICATION_JSON).body(commentService.findByBookId(bookId), CommentDto.class)
            .onErrorResume(BookNotFoundException.class, bookNotFoundFunction(request));
    }

    public Mono<ServerResponse> get(ServerRequest request) {
        long id = Long.parseLong(request.pathVariable(ID));
        return commentService.findById(id)
            .flatMap(body -> ok().contentType(APPLICATION_JSON).bodyValue(body))
            .onErrorResume(CommentNotFoundException.class, commentNotFound(request));
    }

    public Mono<ServerResponse> create(ServerRequest request) {
        long bookId = Long.parseLong(request.pathVariable(BOOK_ID));
        return request.bodyToMono(CommentDto.class)
            .map(book -> book.withBookId(bookId))
            .flatMap(it -> validate(it, request))
            .flatMap(commentService::create)
            .flatMap(body -> status(CREATED).bodyValue(body));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        long id = Long.parseLong(request.pathVariable(ID));
        return request.bodyToMono(CommentDto.class)
            .flatMap(it -> validate(it, request))
            .flatMap(a -> commentService.update(a.withId(id)))
            .flatMap(body -> ok().bodyValue(body))
            .onErrorResume(CommentNotFoundException.class, commentNotFound(request));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        long id = Long.parseLong(request.pathVariable(ID));
        return commentService.deleteById(id)
            .then(noContent().build());
    }

    private Function<CommentNotFoundException, Mono<? extends ServerResponse>> commentNotFound(ServerRequest request) {
        return ex -> {
            var errorText = messageSource.getMessage("error.comment.not-found", null, getLocale(request));
            return status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .bodyValue(new ErrorMessageDto(errorText));
        };
    }

    private Function<BookNotFoundException, Mono<? extends ServerResponse>> bookNotFoundFunction(ServerRequest request) {
        return ex -> {
            var errorText = messageSource.getMessage("error.book.not-found", null, getLocale(request));
            return status(NOT_FOUND)
                .contentType(APPLICATION_JSON)
                .bodyValue(new ErrorMessageDto(errorText));
        };
    }
}
