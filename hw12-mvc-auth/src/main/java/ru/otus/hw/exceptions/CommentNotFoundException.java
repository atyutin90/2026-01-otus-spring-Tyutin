package ru.otus.hw.exceptions;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommentNotFoundException extends RuntimeException {
    private final String message;
}
