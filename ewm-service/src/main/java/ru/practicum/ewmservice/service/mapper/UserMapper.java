package ru.practicum.ewmservice.service.mapper;

import ru.practicum.ewmservice.dto.user.NewUserRequest;
import ru.practicum.ewmservice.dto.user.UserDto;
import ru.practicum.ewmservice.dto.user.UserShortDto;
import ru.practicum.ewmservice.model.User;

public class UserMapper {
    public static UserDto toUserDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(
                user.getId(),
                user.getName()
        );
    }

    public static User toUser(NewUserRequest request) {
        return new User(
                null,
                request.getName(),
                request.getEmail()
        );
    }
}
