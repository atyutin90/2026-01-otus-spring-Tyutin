package ru.otus.hw.controllers;

import org.springframework.security.access.AccessDeniedException;

import java.util.function.Supplier;

abstract class AbstractAccessController {

    protected String accessValidate(Supplier<String> expectSupplier,
                                    Supplier<String> exceptionSupplier) {
        try {
            return expectSupplier.get();
        } catch (AccessDeniedException e) {
            return exceptionSupplier.get();
        }
    }
}
