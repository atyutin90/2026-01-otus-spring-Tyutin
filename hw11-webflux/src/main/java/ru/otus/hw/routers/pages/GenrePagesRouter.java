package ru.otus.hw.routers.pages;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.routers.pages.handlers.GenrePagesHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class GenrePagesRouter {

    @Bean
    public RouterFunction<ServerResponse> genrePagesRoutes(GenrePagesHandler handler) {
        return route()
            .GET("/genres", handler::listPage)
            .GET("/genres/new", handler::newPage)
            .GET("/genres/{id}", handler::showPage)
            .build();
    }
}