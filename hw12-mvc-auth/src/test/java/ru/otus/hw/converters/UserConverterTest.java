package ru.otus.hw.converters;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.hw.dto.UserDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.getDbUsers;

public class UserConverterTest {

    @DisplayName("проверка конвертации User -> UserDto")
    @Test
    void shouldConvertUserToUserDto() {
        var book = getDbUsers().stream().findFirst().orElse(null);
        var expectedDtoUser = new UserDto(book.getId(), book.getUsername(), null);
        assertEquals(UserConverter.userDtoOf(book), expectedDtoUser);
    }
}
