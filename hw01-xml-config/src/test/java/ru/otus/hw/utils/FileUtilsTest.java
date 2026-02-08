package ru.otus.hw.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class FileUtilsTest {

    @Test
    void getResourceInputStreamWhenFileExists() {
        var file = "test.csv";
        try (var inputStream = FileUtils.getResourceInputStream(file)) {
            assertNotNull(inputStream);
        } catch (IOException e) {
            //Nothing
        }
    }

    @Test
    void getResourceInputStreamWhenFileDoesNotExist(){
        var file = "no_test.csv";
        var exception = assertThrows(FileNotFoundException.class, () -> FileUtils.getResourceInputStream(file));
        assertTrue(exception.getMessage().contains(file));
    }
}
