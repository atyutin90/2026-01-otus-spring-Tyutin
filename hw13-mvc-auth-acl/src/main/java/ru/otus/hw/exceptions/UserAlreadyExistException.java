package ru.otus.hw.exceptions;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UserAlreadyExistException extends RuntimeException {
    private final String message;
}
