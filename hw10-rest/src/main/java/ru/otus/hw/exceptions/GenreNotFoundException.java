package ru.otus.hw.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GenreNotFoundException extends RuntimeException {
    private final String message;
}
