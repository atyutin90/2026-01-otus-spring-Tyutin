package ru.otus.hw.service;

import com.opencsv.bean.CsvToBeanBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.hw.domain.Caterpillar;
import ru.otus.hw.exceptions.FileReadException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static ru.otus.hw.utils.FileUtils.getResourceInputStream;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final int SKIP_LINES = 1;

    private static final char COMMA = ';';

    @Override
    public List<Caterpillar> getCaterpillarsData(String fileName) {
        List<Caterpillar> caterpillars;
        try (var inputFile = new InputStreamReader(getResourceInputStream(fileName), UTF_8)) {
            caterpillars = new CsvToBeanBuilder<Caterpillar>(inputFile)
                .withType(Caterpillar.class)
                .withSkipLines(SKIP_LINES)
                .withSeparator(COMMA)
                .withIgnoreLeadingWhiteSpace(true)
                .build()
                .parse();

        } catch (IOException e) {
            throw new FileReadException("Error reading file", e);
        }
        return caterpillars;
    }
}
