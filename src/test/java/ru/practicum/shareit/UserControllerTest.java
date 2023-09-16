package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {
	private UserDto testUser;
	@Autowired
	private UserController userController;
	@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
	@Autowired
	private MockHttpServletRequest request;

	@BeforeEach
	void createContext() {
		testUser = new UserDto(
				1,
				"user",
				"user@yandex.ru"
		);
	}

	@Test
	void testBlankEmail() {
		testUser.setEmail("");

		ValidationException exception = assertThrows(
				ValidationException.class,
				() -> userController.create(testUser, request)
		);

		assertEquals("Incorrect email " + testUser.getEmail(), exception.getMessage());
	}

	@Test
	void testEmailContainsSymbol() {
		testUser.setEmail("memelover@gmail.com@");

		ValidationException exception = assertThrows(
				ValidationException.class,
				() -> userController.create(testUser, request)
		);

		assertEquals("Incorrect email " + testUser.getEmail(), exception.getMessage());
	}

	@Test
	void testBlankName() {
		testUser.setName("");

		ValidationException exception = assertThrows(
				ValidationException.class,
				() -> userController.create(testUser, request)
		);

		assertEquals("Incorrect name " + testUser.getName(), exception.getMessage());
	}

	@Test
	void testNameContainsSpaces() {
		testUser.setName("Super JavaProgrammer2000");

		ValidationException exception = assertThrows(
				ValidationException.class,
				() -> userController.create(testUser, request)
		);

		assertEquals("Incorrect name " + testUser.getName(), exception.getMessage());
	}

	@Test
	void testDuplicateEmail() {
		UserDto newUser = new UserDto(
				2,
				"user2",
				"user@yandex.ru"
		);

		userController.create(testUser, request);

		ValidationException exception = assertThrows(
				ValidationException.class,
				() -> userController.create(newUser, request)
		);

		assertEquals("Duplicate email " + newUser.getEmail(), exception.getMessage());
	}

	@Test
	void testUserWithWrongId(){
		userController.create(testUser, request);

		UserNotFoundException exception = assertThrows(
				UserNotFoundException.class,
				() -> userController.getUserById(100, request)
		);

		assertEquals("User was not found", exception.getMessage());
	}

	@Test
	void patchUser(){
		UserDto updatedUser = new UserDto(
				1,
				"update",
				null
		);

		userController.create(testUser, request);

		userController.update(updatedUser,1,request);

		UserDto userAfterUpdate = userController.getUserById(testUser.getId(), request);

		assertEquals(userAfterUpdate.getName(), updatedUser.getName());
	}

	@Test
	void patchUserEmailExists(){
		UserDto updatedUser = new UserDto(
				1,
				null,
				"user@user.ru"
		);
		UserDto testUser2 = new UserDto(
				2,
				"user",
				"user@user.ru"
		);

		userController.create(testUser, request);
		userController.create(testUser2, request);

		UserAlreadyExistsException exception = assertThrows(
				UserAlreadyExistsException.class,
				() -> userController.update(updatedUser, 1, request)
		);

		assertEquals("User with provided email " + updatedUser.getEmail() + " already exists", exception.getMessage());
	}

	@Test
	void testGetUserById(){
		UserDto testUser2 = new UserDto(
				2,
				"user",
				"user@user.ru"
		);
		userController.create(testUser, request);
		UserDto user2 = userController.create(testUser2, request);
		userController.getUserById(user2.getId(), request);

		assertEquals(2, user2.getId());
	}

	@Test
	public void testDeleteUser() {
		UserDto user = userController.create(testUser, request);
		int actualSizeList = userController.getUsers().size();
		assertEquals(1, actualSizeList);

		userController.delete(user.getId(), request);
		actualSizeList = userController.getUsers().size();

		assertEquals(0, actualSizeList);
	}
}
