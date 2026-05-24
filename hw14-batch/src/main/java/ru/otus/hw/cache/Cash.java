package ru.otus.hw.cache;

public interface Cash<T> {

    T get(Long key);

    void put(Long key, T value);

    void clear();
}
