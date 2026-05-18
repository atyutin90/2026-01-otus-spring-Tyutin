package ru.otus.hw.batch.writer;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.data.mongodb.core.MongoTemplate;
import ru.otus.hw.cache.Cash;
import ru.otus.hw.models.jpa.CommonEntity;

@RequiredArgsConstructor
public class ItemMongoWriter<T extends CommonEntity, V> implements ItemWriter<Pair<T, V>> {

    private final MongoTemplate mongoTemplate;

    private final Cash<V> cash;

    @Override
    public void write(Chunk<? extends Pair<T, V>> chunk) throws Exception {
        chunk.getItems().forEach(item -> {
            var doc = item.getValue();
            mongoTemplate.insert(doc);
            cash.put(item.getKey().getId(), doc);
        });
    }
}
