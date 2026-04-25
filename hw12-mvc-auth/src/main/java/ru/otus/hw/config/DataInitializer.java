package ru.otus.hw.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.util.Pair;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.RoleRepository;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {

        var adminRole = roleRepository.findByName("ADMIN").orElseThrow();

        var userRole = roleRepository.findByName("USER").orElseThrow();

        var users = List.of(
            Pair.of("admin", List.of(adminRole, userRole)),
            Pair.of("user", List.of(userRole))
        );

        users.forEach(user -> {
            if (userRepository.findByUsername(user.getFirst()).isEmpty()) {
                userRepository.save(
                    User.builder()
                        .username(user.getFirst())
                        .password(passwordEncoder.encode(user.getFirst()))
                        .roles(user.getSecond())
                        .build()
                );
            }
        });
    }
}
