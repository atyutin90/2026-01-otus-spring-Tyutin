package ru.otus.hw.routers.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.routers.rest.handlers.BookHandler;

@Configuration
public class BookRouter {

    @Bean
    public RouterFunction<ServerResponse> bookRoutes(BookHandler handler) {
        return RouterFunctions.route()
            .GET("/api/books", handler::list)
            .POST("/api/books", handler::create)
            .GET("/api/books/{id}", handler::get)
            .PATCH("/api/books/{id}", handler::update)
            .DELETE("/api/books/{id}", handler::delete)
            .build();
    }
}
