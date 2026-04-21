package ru.otus.hw.routers.pages.handlers;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@Component
public class GenrePagesHandler extends AbstractPageHandler {

    public Mono<ServerResponse> listPage(ServerRequest request) {
        return ok().render("genre/list");
    }

    public Mono<ServerResponse> newPage(ServerRequest request) {
        return ok().render("genre/show");
    }

    public Mono<ServerResponse> showPage(ServerRequest request) {
        return Mono.fromCallable(() -> Long.parseLong(request.pathVariable("id")))
            .flatMap(id -> ok().render("genre/show"))
            .onErrorMap(NumberFormatException.class, getInvalidId());
    }
}
