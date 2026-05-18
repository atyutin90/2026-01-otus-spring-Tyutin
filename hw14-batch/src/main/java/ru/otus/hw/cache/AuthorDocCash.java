package ru.otus.hw.cache;

import lombok.ToString;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.AuthorDoc;

import java.util.concurrent.ConcurrentHashMap;

@ToString
@Component
public class AuthorDocCash implements Cash<AuthorDoc> {

    private final ConcurrentHashMap<Long, AuthorDoc> cache = new ConcurrentHashMap<>();

    public AuthorDoc get(Long key) {
        return cache.get(key);
    }

    public void put(Long key, AuthorDoc value) {
        cache.putIfAbsent(key, value);
    }

    public void clear() {
        cache.clear();
    }
}
