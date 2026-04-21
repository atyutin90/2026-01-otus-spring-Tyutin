package ru.otus.hw.routers.pages;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.routers.pages.handlers.AuthorPagesHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class AuthorPagesRouter {

    @Bean
    public RouterFunction<ServerResponse> authorPagesRoutes(AuthorPagesHandler handler) {
        return route()
            .GET("/authors", handler::listPage)
            .GET("/authors/new", handler::newPage)
            .GET("/authors/{id}", handler::showPage)
            .build();
    }
}