package ru.otus.hw.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.converters.UserConverter;
import ru.otus.hw.dto.UserDto;
import ru.otus.hw.exceptions.UserAlreadyExistException;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.RoleRepository;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto findByUsername(String username) {
        return userRepository.findByUsername(username).map(UserConverter::userDtoOf).orElse(null);
    }

    @Transactional
    @Override
    public void register(UserDto userDto) {
        var oUser = userRepository.findByUsername(userDto.username());
        if (oUser.isPresent()) {
            throw new UserAlreadyExistException("User with username: %s exist".formatted(userDto.username()));
        }
        var defaultRoles = roleRepository.findByName("USER").map(List::of).orElse(List.of());

        userRepository.save(User.builder()
            .username(userDto.username())
            .password(passwordEncoder.encode(userDto.password()))
            .roles(defaultRoles)
            .build());
    }
}
