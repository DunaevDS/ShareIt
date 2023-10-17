package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.transaction.Transactional;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDto create(UserDto userDto) {
        if (userDto == null) {
            log.error("EmptyObjectException: User is null.");
            throw new UserNotFoundException("User was not provided");
        }
        validationUserCreation(userDto);
        try {
            return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            log.error("User with email='{}' already exists", userDto.getEmail());
            throw new UserAlreadyExistsException("User with email = " + userDto.getEmail() + " already exists");
        }
    }

    @Override
    public UserDto update(UserDto userDto, Integer id) {
        if (userDto == null) {
            log.error("EmptyObjectException: User is null.");
            throw new UserNotFoundException("User was not provided");
        }

        validationUserUpdate(userDto);
        userDto.setId(id);

        User user = userRepository.findById(id).orElseThrow(() -> throwUserNotFoundException(
                "NotFoundException: Item with id= " + id + " was not found."));

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        if ((userDto.getEmail() != null) && (!userDto.getEmail().equals(user.getEmail()))) {
            if (userRepository.findByEmail(userDto.getEmail())
                    .stream()
                    .filter(u -> u.getEmail().equals(userDto.getEmail()))
                    .allMatch(u -> u.getId().equals(userDto.getId()))) {
                user.setEmail(userDto.getEmail());
            } else {
                log.error("User with email='{}' already exists", userDto.getEmail());
                throw new UserAlreadyExistsException("User with email = " + userDto.getEmail() + " already exists");
            }
        }
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public void delete(Integer userId) {
        if (!userRepository.existsById(userId)) {
            log.error("NotFoundException: User with id='{}' was not found.", userId);
            throw new UserNotFoundException("User was not found");
        }

        userRepository.deleteById(userId);
    }

    @Override
    public List<UserDto> getUsers() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .collect(toList());
    }

    @Override
    public UserDto findUserById(Integer userId) {
        return UserMapper.mapToUserDto(userRepository.findById(userId).orElseThrow(() -> throwUserNotFoundException(
                "NotFoundException: User with id= " + userId + " was not found.")));
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

    private UserNotFoundException throwUserNotFoundException(String message) {
        log.error(message);
        throw new UserNotFoundException(message);
    }
}
