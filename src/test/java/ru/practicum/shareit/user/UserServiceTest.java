package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest {
    private final UserService userService;

    private User user = new User(1, "Apollon", "bestJavaProgrammer@yandex.ru");

    @Test
    void test_GetUserById() {
        UserDto returnUserDto = userService.create(UserMapper.mapToUserDto(user));
        assertThat(returnUserDto.getName(), equalTo(user.getName()));
        assertThat(returnUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void test_DeleteUserWithWrongId() {
        NotFoundException exp = assertThrows(NotFoundException.class, () -> userService.delete(10));
        assertEquals("User was not found", exp.getMessage());
    }

    @Test
    void test_DeleteUser() {
        User user = new User(10, "Homer", "bestKotlinProgrammer@yandex.ru");
        UserDto returnUserDto = userService.create(UserMapper.mapToUserDto(user));
        List<UserDto> listUser = userService.getUsers();
        int size = listUser.size();
        userService.delete(returnUserDto.getId());
        listUser = userService.getUsers();
        assertThat(listUser.size(), equalTo(size - 1));
    }

    @Test
    void test_UpdateUser() {
        UserDto returnUserDto = userService.create(UserMapper.mapToUserDto(user));
        returnUserDto.setName("updatedName");
        returnUserDto.setEmail("updatedEmail@email.ru");
        userService.update(returnUserDto, returnUserDto.getId());
        UserDto updateUserDto = userService.findUserById(returnUserDto.getId());
        assertThat(updateUserDto.getName(), equalTo("updatedName"));
        assertThat(updateUserDto.getEmail(), equalTo("updatedEmail@email.ru"));
    }

    @Test
    void test_UpdateUserWithExistEmail() {
        user = new User(2, "Homer", "bestKotlinProgrammer@yandex.ru");
        userService.create(UserMapper.mapToUserDto(user));
        User newUser = new User(3, "Bart", "bestSwiftProgrammer@yandex.ru");
        UserDto returnUserDto = userService.create(UserMapper.mapToUserDto(newUser));
        Integer id = returnUserDto.getId();
        returnUserDto.setId(null);
        returnUserDto.setEmail("bestKotlinProgrammer@yandex.ru");

        final ConflictException exception = Assertions.assertThrows(
                ConflictException.class,
                () -> userService.update(returnUserDto, id));

        Assertions.assertEquals("User with email = " + returnUserDto.getEmail() + " already exists",
                exception.getMessage());
    }
}

