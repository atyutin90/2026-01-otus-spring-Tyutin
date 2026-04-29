package ru.otus.hw.converters;

import ru.otus.hw.dto.UserDto;

import ru.otus.hw.models.User;

public class UserConverter {

    public static UserDto userDtoOf(User user) {
        return new UserDto(user.getId(), user.getUsername(), null);
    }
}
