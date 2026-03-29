package ru.otus.hw.services;

import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.context.annotation.Import;
import ru.otus.hw.MongoDataTestDataInitializer;

@DataMongoTest
@Import(MongoDataTestDataInitializer.class)
public class AbstractServiceTest {
}
