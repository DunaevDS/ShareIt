package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, int id);

    UserDto delete(int userId);

    UserDto getUserById(int userId);

    List<UserDto> getUsers();

    boolean existsById(int userId);
}