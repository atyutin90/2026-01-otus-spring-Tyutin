package ru.otus.hw.cache;

import lombok.ToString;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.BookDoc;

import java.util.concurrent.ConcurrentHashMap;

@ToString
@Component
public class BookDocCash implements Cash<BookDoc> {

    private final ConcurrentHashMap<Long, BookDoc> cache = new ConcurrentHashMap<>();

    public BookDoc get(Long key) {
        return cache.get(key);
    }

    public void put(Long key, BookDoc value) {
        cache.putIfAbsent(key, value);
    }

    public void clear() {
        cache.clear();
    }
}
