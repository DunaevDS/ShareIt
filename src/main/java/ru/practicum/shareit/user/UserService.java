package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto create(UserDto userDto);

    UserDto update(UserDto userDto, Integer id);

    void delete(Integer userId);

    List<UserDto> getUsers();

    UserDto findUserById(Integer userId);

}