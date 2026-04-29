package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.DataTest.NEW_USER_PASSWORD;
import static ru.otus.hw.DataTest.NEW_USER_USERNAME;
import static ru.otus.hw.DataTest.getDbRoles;
import static ru.otus.hw.DataTest.getDbUsers;

@DisplayName("Репозиторий на основе JPA для работы с пользователями")
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository repository;

    private List<User> dbUsers;

    @BeforeEach
    void setUp() {
        dbUsers = getDbUsers();
    }

    @DisplayName("должен загружать пользователя по username")
    @Test
    void shouldReturnCorrectUserByUsername() {
        var username = "user";
        var expected = dbUsers.stream().filter(u -> u.getUsername().equals(username)).findFirst().orElseThrow();
        var returnedUser = repository.findByUsername(username).orElseThrow();
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser).isEqualTo(expected);
        assertThat(returnedUser.getId()).isEqualTo(expected.getId());
        assertThat(returnedUser.getPassword()).isEqualTo(expected.getPassword());
        assertThat(returnedUser.getUsername()).isEqualTo(expected.getUsername());
        assertThat(returnedUser.getRoles()).containsExactlyElementsOf(expected.getRoles());
    }

    @DisplayName("должен сохранять нового пользователя")
    @Test
    void shouldSaveNewUser() {
        var expected = User.builder()
            .password(NEW_USER_PASSWORD)
            .username(NEW_USER_USERNAME)
            .roles(List.of(getDbRoles().get(0)))
            .build();

        var returnedUser = repository.save(expected);

        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser).isEqualTo(expected);
        assertThat(returnedUser.getId()).isEqualTo(expected.getId());
        assertThat(returnedUser.getPassword()).isEqualTo(expected.getPassword());
        assertThat(returnedUser.getUsername()).isEqualTo(expected.getUsername());
        assertThat(returnedUser.getRoles()).containsExactlyElementsOf(expected.getRoles());
    }
}
