package ru.otus.hw.services;

import ru.otus.hw.dto.UserDto;

public interface UserService {

    UserDto findByUsername(String username);

    void register(UserDto userDto);
}
