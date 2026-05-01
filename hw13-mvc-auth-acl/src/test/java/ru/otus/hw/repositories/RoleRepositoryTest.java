package ru.otus.hw.repositories;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.otus.hw.models.Role;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.otus.hw.DataTest.getDbRoles;

@DisplayName("Репозиторий на основе JPA для работы с ролями")
@DataJpaTest
public class RoleRepositoryTest {

    @Autowired
    private RoleRepository repository;

    private List<Role> dsRoles;

    @BeforeEach
    void setUp() {
        dsRoles = getDbRoles();
    }

    @DisplayName("должен загружать роль по наименованию")
    @Test
    void shouldReturnCorrectRoleByName() {
        var name = "ADMIN";
        var expected = dsRoles.stream().filter(u -> u.getName().equals(name)).findFirst().orElseThrow();
        var returnedUser = repository.findByName(name).orElseThrow();
        assertThat(returnedUser).isNotNull();
        assertThat(returnedUser).isEqualTo(expected);
        assertThat(returnedUser.getId()).isEqualTo(expected.getId());
        assertThat(returnedUser.getName()).isEqualTo(expected.getName());
    }
}
