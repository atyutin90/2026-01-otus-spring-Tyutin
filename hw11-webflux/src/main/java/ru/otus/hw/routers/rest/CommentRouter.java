package ru.otus.hw.routers.rest;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.routers.rest.handlers.CommentHandler;

@Configuration
public class CommentRouter {

    @Bean
    public RouterFunction<ServerResponse> commentRoutes(CommentHandler handler) {
        return RouterFunctions.route()
            .GET("/api/books/{bookId}/comments", handler::list)
            .POST("/api/books/{bookId}/comments", handler::create)
            .GET("/api/comments/{id}", handler::get)
            .PATCH("/api/comments/{id}", handler::update)
            .DELETE("/api/comments/{id}", handler::delete)
            .build();
    }
}
