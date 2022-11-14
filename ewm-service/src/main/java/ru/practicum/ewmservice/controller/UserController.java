package ru.practicum.ewmservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewmservice.dto.*;
import ru.practicum.ewmservice.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto addUser(@RequestBody @Valid NewUserRequest request) {
        log.info("Get request for adding user {} with email {}", request.getName(), request.getEmail());
        return userService.add(request);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable @PositiveOrZero Long userId,
                              @RequestBody @Valid UserDto userDto) {
        log.info("Get request for updating user with id={}", userId);
        return userService.update(userId, userDto);
    }

    @GetMapping
    public List<UserDto> getAllUsers(
            @RequestParam List<Long> ids,
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        log.info("Get request for getting all users.");
        return userService.getAll(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable @PositiveOrZero long userId) {
        userService.delete(userId);
        log.info("User with id={} deleted successfully.", userId);
    }
}
