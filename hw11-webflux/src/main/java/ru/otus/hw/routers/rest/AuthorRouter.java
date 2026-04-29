package ru.otus.hw.routers.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.routers.rest.handlers.AuthorHandler;

@Configuration
public class AuthorRouter {

    @Bean
    public RouterFunction<ServerResponse> authorRoutes(AuthorHandler handler) {
        return RouterFunctions.route()
            .GET("/api/authors", handler::list)
            .POST("/api/authors", handler::create)
            .GET("/api/authors/{id}", handler::get)
            .PATCH("/api/authors/{id}", handler::update)
            .DELETE("/api/authors/{id}", handler::delete)
            .build();
    }
}
