package ru.practicum.ewmservice.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmservice.dto.user.NewUserRequest;
import ru.practicum.ewmservice.dto.user.UserDto;
import ru.practicum.ewmservice.exception.BadRequestException;
import ru.practicum.ewmservice.model.User;
import ru.practicum.ewmservice.repository.UserRepository;
import ru.practicum.ewmservice.service.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto add(NewUserRequest request) {
        final User user = UserMapper.toUser(request);
        final User savedUser = userRepository.save(user);
        final UserDto newDto = UserMapper.toUserDto(savedUser);
        log.info("New user with id {}, name {} & email {} created.",
                newDto.getId(),
                newDto.getName(),
                newDto.getEmail());

        return newDto;
    }

    @Override
    public UserDto get(long userId) {
        final User user = getUserFromRepo(userId);
        UserDto resultUserDto = UserMapper.toUserDto(user);
        log.info("Getting user with id={}, name {} & email {}", user.getId(), user.getName(), user.getEmail());

        return resultUserDto;
    }

    @Override
    public List<UserDto> getAll(List<Long> userIds, Integer from, Integer size) {
        List<User> resultList;
        if (userIds.isEmpty()) {
            resultList = userRepository.findAll(PageRequest.of(from / size, size)).toList();
        } else {
            resultList = userRepository.findAllById(userIds);
        }
        return resultList.stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto update(long userId, UserDto userDto) {
        final User userToUpdate = getUserFromRepo(userId);
        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userToUpdate.setEmail(userToUpdate.getEmail());
        }

        User resultUser = userRepository.save(userToUpdate);
        log.info("User with if {}, name {} & email {} updated successfully.",
                resultUser.getId(), resultUser.getName(), resultUser.getEmail());

        return UserMapper.toUserDto(resultUser);
    }

    @Override
    @Transactional
    public void delete(long userId) {
        userRepository.deleteById(userId);
        log.info("User with id {} deleted successfully", userId);
    }

    private User getUserFromRepo(long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new BadRequestException(
                String.format("There isn't user with id %d in this database.", userId)
        ));
    }
}
