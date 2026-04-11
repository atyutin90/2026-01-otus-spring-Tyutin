package ru.otus.hw.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AuthorNotFoundException extends RuntimeException {
    private final String message;
}
