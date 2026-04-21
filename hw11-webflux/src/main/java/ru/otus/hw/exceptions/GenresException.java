package ru.otus.hw.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GenresException extends RuntimeException {
    private final String message;
}
