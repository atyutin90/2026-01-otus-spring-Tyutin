package ru.otus.hw.cache;

import lombok.ToString;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.mongo.GenreDoc;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@ToString
@Component
public class GenreDocCash implements Cash<GenreDoc> {

    private final ConcurrentHashMap<Long, GenreDoc> cache = new ConcurrentHashMap<>();

    public GenreDoc get(Long key) {
        return cache.get(key);
    }

    public List<GenreDoc> get(List<Long> keys) {
        return keys.stream().map(cache::get).toList();
    }

    public void put(Long key, GenreDoc value) {
        cache.putIfAbsent(key, value);
    }

    public void clear() {
        cache.clear();
    }
}
