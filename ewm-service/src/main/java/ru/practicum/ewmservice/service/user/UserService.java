package ru.practicum.ewmservice.service.user;

import ru.practicum.ewmservice.dto.user.NewUserRequest;
import ru.practicum.ewmservice.dto.user.UserDto;

import java.util.List;

public interface UserService {
    UserDto add(NewUserRequest request);

    UserDto get(long userId);

    List<UserDto> getAll(List<Long> userIds, Integer from, Integer size);

    UserDto update(long userId, UserDto userDto);

    void delete(long userId);
}
