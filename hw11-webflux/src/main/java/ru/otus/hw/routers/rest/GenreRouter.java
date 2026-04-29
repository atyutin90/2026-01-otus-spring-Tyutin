package ru.otus.hw.routers.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.routers.rest.handlers.GenreHandler;

@Configuration
public class GenreRouter {

    @Bean
    public RouterFunction<ServerResponse> genreRoutes(GenreHandler handler) {
        return RouterFunctions.route()
            .GET("/api/genres", handler::list)
            .POST("/api/genres", handler::create)
            .GET("/api/genres/{id}", handler::get)
            .PATCH("/api/genres/{id}", handler::update)
            .DELETE("/api/genres/{id}", handler::delete)
            .build();
    }
}
