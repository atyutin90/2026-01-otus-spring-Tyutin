package ru.otus.hw.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BookNotFoundException extends RuntimeException {
    private final String message;
}
