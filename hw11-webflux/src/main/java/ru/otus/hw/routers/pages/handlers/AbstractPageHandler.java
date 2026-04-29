package ru.otus.hw.routers.pages.handlers;

import org.springframework.web.server.ServerWebInputException;

import java.util.function.Function;

public abstract class AbstractPageHandler {

    protected static Function<NumberFormatException, Throwable> getInvalidId() {
        return e -> new ServerWebInputException("Invalid id");
    }
}
