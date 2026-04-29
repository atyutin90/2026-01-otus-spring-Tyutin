package ru.otus.hw.routers.pages;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.routers.pages.handlers.CommentPagesHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class CommentPagesRouter {

    @Bean
    public RouterFunction<ServerResponse> commentPagesRoutes(CommentPagesHandler handler) {
        return route()
            .GET("/books/{bookId}/comments", handler::newPage)
            .GET("/books/{bookId}/comments/{id}", handler::showPage)
            .build();
    }
}