package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Integer id);

    void delete(Integer userId);

    UserDto getUserById(Integer userId);

    List<UserDto> getUsers();

    User findUserById(Integer userId);
}