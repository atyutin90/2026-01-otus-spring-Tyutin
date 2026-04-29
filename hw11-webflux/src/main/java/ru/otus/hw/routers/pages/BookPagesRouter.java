package ru.otus.hw.routers.pages;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import ru.otus.hw.routers.pages.handlers.BookPagesHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BookPagesRouter {

    @Bean
    public RouterFunction<ServerResponse> bookPagesRoutes(BookPagesHandler handler) {
        return route()
            .GET("/books", handler::listPage)
            .GET("/books/new", handler::newPage)
            .GET("/books/{id}", handler::showPage)
            .build();
    }
}