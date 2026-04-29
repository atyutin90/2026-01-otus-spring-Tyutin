package ru.otus.hw.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.otus.hw.converters.GenreConverter;
import ru.otus.hw.converters.UserConverter;
import ru.otus.hw.converters.UserConverterTest;
import ru.otus.hw.dto.GenreDto;
import ru.otus.hw.dto.UserDto;
import ru.otus.hw.exceptions.GenreNotFoundException;
import ru.otus.hw.models.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.otus.hw.DataTest.NEW_COMMENT_TEXT;
import static ru.otus.hw.DataTest.NEW_USER_PASSWORD;
import static ru.otus.hw.DataTest.NEW_USER_USERNAME;
import static ru.otus.hw.DataTest.getDbGenres;
import static ru.otus.hw.DataTest.getDbUsers;

@DisplayName("Сервис для работы с пользователями ")
@DataJpaTest
@Import({UserServiceImpl.class, BCryptPasswordEncoder.class})
public class UserServiceImplTest {

    @Autowired
    private UserService userService;

    private List<UserDto> userDtoList;

    @BeforeEach
    void setUp() {
        userDtoList = getDbUsers().stream().map(UserConverter::userDtoOf).toList();
    }

    @DisplayName("должен загружать пользователя по username")
    @Test
    void shouldReturnCorrectUserByUsername() {
        var id = 1L;
        var expect = userDtoList.stream()
                .filter(it -> it.id().equals(id))
                .findFirst()
                .orElse(null);
        var dto = userService.findByUsername(expect.username());
        assertEquals(expect, dto);
    }

    @DisplayName("должен зарегистрировать нового пользователя")
    @Test
    void shouldRegisterNewUser() {
        var expected = UserDto.builder()
            .username(NEW_USER_USERNAME)
            .password(NEW_USER_PASSWORD)
            .build();

        userService.register(expected);

        var result = userService.findByUsername(expected.username());

        assertThat(result).isNotNull();
        assertThat(result.username()).isEqualTo(expected.username());
        assertThat(result.id()).isGreaterThan(0);
    }
}
