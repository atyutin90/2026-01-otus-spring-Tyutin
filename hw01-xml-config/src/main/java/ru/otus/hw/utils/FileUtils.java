package ru.otus.hw.utils;

import lombok.experimental.UtilityClass;

import java.io.FileNotFoundException;
import java.io.InputStream;

import static java.lang.String.format;

@UtilityClass
public class FileUtils {

    /**
     * Load resource from classpath.
     *
     * @param fileName full filename from resource
     * @return {@link InputStream}
     * @throws FileNotFoundException file not found в classpath
     * @example Example:
     * <pre>{@code
     * InputStream is1 = FileUtils.getResourceInputStream("test.txt");
     * }</pre>
     */
    public static InputStream getResourceInputStream(String fileName) throws FileNotFoundException {
        var classLoader = FileUtils.class.getClassLoader();
        var inputStream = classLoader.getResourceAsStream(fileName);
        if (inputStream != null) {
            return inputStream;
        } else {
            throw new FileNotFoundException(format("File not found in classpath: %s", fileName));
        }
    }
}
