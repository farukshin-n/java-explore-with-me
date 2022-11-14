package ru.practicum.ewmservice.service;

import ru.practicum.ewmservice.dto.NewUserRequest;
import ru.practicum.ewmservice.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(NewUserRequest request);

    UserDto get(long userId);

    List<UserDto> getAll(List<Long> userIds, Integer from, Integer size);

    UserDto update(long userId, UserDto userDto);

    void delete(long userId);
}
