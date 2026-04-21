package ru.otus.hw.routers.pages.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class CommentPagesHandler extends AbstractPageHandler {

    public Mono<ServerResponse> newPage(ServerRequest request) {
        return ok().render("comment/show");
    }

    public Mono<ServerResponse> showPage(ServerRequest request) {
        return Mono.fromCallable(() -> Long.parseLong(request.pathVariable("id")))
            .flatMap(id -> ok().render("comment/show"))
            .onErrorMap(NumberFormatException.class, getInvalidId());
    }
}
