package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemStorage;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final ItemStorage itemStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("UserStorage") UserStorage userStorage,
                           ItemStorage itemStorage) {
        this.userStorage = userStorage;
        this.itemStorage = itemStorage;
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto == null) {
            log.error("EmptyObjectException: User is null.");
            throw new UserNotFoundException("User was not provided");
        }

        validationUserCreation(userDto);

        return UserMapper.mapToUserDto(
                userStorage.create(UserMapper.mapToUser(userDto))
        );
    }

    @Override
    public UserDto update(UserDto userDto, int id) {
        if (userDto == null) {
            log.error("EmptyObjectException: User is null.");
            throw new UserNotFoundException("User was not provided");
        }
        if (!userStorage.existsById(id)) {
            log.error("NotFoundException: User with id='{}' was not found.", id);
            throw new UserNotFoundException("User was not found.");
        }

        validationUserUpdate(userDto);
        userDto.setId(id);

        return UserMapper.mapToUserDto(
                userStorage.update(UserMapper.mapToUser(userDto))
        );
    }

    @Override
    public UserDto delete(int userId) {
        if (!userStorage.existsById(userId)) {
            log.error("NotFoundException: User with id='{}' was not found.", userId);
            throw new UserNotFoundException("User was not found");
        }
        itemStorage.deleteAllItems(userId);
        return UserMapper.mapToUserDto(userStorage.delete(userId));
    }

    @Override
    public UserDto getUserById(int userId) {
        User user = userStorage.getUserById(userId);
        if (user == null) {
            log.error("NotFoundException: User with id='{}' was not found.", userId);
            throw new UserNotFoundException("User was not found");
        }

        return UserMapper.mapToUserDto(user);
    }

    @Override
    public List<UserDto> getUsers() {
        return userStorage.getUsers().stream()
                .map(UserMapper::mapToUserDto)
                .collect(toList());
    }

    @Override
    public boolean existsById(int userId) {
        return userStorage.existsById(userId);
    }

    private void validationUserCreation(UserDto userDto) {
        if (userDto.getEmail().isBlank() || !isValidEmail(userDto.getEmail())) {
            log.error("ValidationException: incorrect email");
            throw new ValidationException("Incorrect email " + userDto.getEmail());
        }
        if ((userDto.getName().isBlank()) || (userDto.getName().contains(" "))) {
            log.error("ValidationException: incorrect login");
            throw new ValidationException("Incorrect name " + userDto.getName());
        }
        if (userStorage.existsByEmail(userDto.getEmail())) {
            log.error("ValidationException: duplicate email");
            throw new ValidationException("Duplicate email " + userDto.getEmail());
        }
    }

    private void validationUserUpdate(UserDto userDto) {
        if (userDto.getEmail() != null) {
            if (!isValidEmail(userDto.getEmail())) {
                log.error("ValidationException: incorrect email");
                throw new ValidationException("Incorrect email " + userDto.getEmail());
            }
        }
        if (userDto.getName() != null) {
            if ((userDto.getName().contains(" "))) {
                log.error("ValidationException: incorrect login");
                throw new ValidationException("Incorrect name " + userDto.getName());
            }
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        return email.matches(emailRegex);
    }
}
