package ru.practicum.shareit.user;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository mockUserRepository;
    private UserService userService;
    private final UserDto userDto = new UserDto(1, "Apollon", "bestJavaProgrammer@yandex.ru");

    @BeforeEach
    void beforeEach() {
        userService = new UserServiceImpl(mockUserRepository);
    }

    @Test
    void test_GetUserWithWrongId() {
        when(mockUserRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(
                NotFoundException.class,

                () -> userService.findUserById(-1));
        Assertions.assertEquals("NotFoundException: User with id= -1 was not found.", exception.getMessage());
    }

    @Test
    void test_CreateUserWithExistEmail() {
        when(mockUserRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException(""));

        final ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.create(userDto));

        Assertions.assertEquals("User with email = " + userDto.getEmail() + " already exists",
                exception.getMessage());
    }

    @Test
    void test_FindUserById() {
        when(mockUserRepository.findById(any(Integer.class)))
                .thenReturn(Optional.of(UserMapper.mapToUser(userDto)));

        User user = UserMapper.mapToUser(userService.findUserById(1));

        verify(mockUserRepository, Mockito.times(1))
                .findById(1);

        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void create_ThrowsConflictException_WhenInvalidEmail() {
        userDto.setEmail("invalidEmail@@");
        assertThrows(ConflictException.class, () -> userService.create(userDto));
    }

    @Test
    void create_ThrowsConflictException_WhenInvalidName() {
        userDto.setName(" ");
        assertThrows(ConflictException.class, () -> userService.update(userDto, userDto.getId()));
    }

}
