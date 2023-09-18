package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;


import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ItemControllerTest {
    private UserDto testUser;
    private ItemDto testItem;
    @Autowired
    private UserController userController;
    @Autowired
    private ItemController itemController;
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

        testItem = new ItemDto(
                1,
                "Дрель",
                "Дрель обыкновенная",
                true,
                null
        );
    }

    @Test
    void testBlankDescription() {
        testItem.setDescription("");
        userController.create(testUser, request);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemController.create(testItem, 1, request)
        );

        assertEquals("Incorrect description " + testItem.getDescription(), exception.getMessage());
    }

    @Test
    void testBlankName() {
        userController.create(testUser, request);
        testItem.setName("");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemController.create(testItem, 1, request)
        );

        assertEquals("Incorrect name " + testItem.getName(), exception.getMessage());
    }

    @Test
    void testNameContainsSpaces() {
        userController.create(testUser, request);
        testItem.setName("Best thing");

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemController.create(testItem, 1, request)
        );

        assertEquals("Incorrect name " + testItem.getName(), exception.getMessage());
    }

    @Test
    void testWrongItemOwner() {
        userController.create(testUser, request);
        int ownerId = 10;

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemController.create(testItem, ownerId, request)
        );

        assertEquals("User with ID = " + ownerId + " was not found.", exception.getMessage());
    }

    @Test
    void patchItem() {
        userController.create(testUser, request);
        ItemDto updatetedItem = new ItemDto(
                1,
                "Насос",
                "Насос обыкновенный",
                false,
                null
        );

        itemController.create(testItem, 1, request);

        itemController.update(updatetedItem, 1, 1, request);

        ItemDto itemAfterUpdate = itemController.getItemById(testItem.getId(), request);

        assertEquals(itemAfterUpdate.getName(), updatetedItem.getName());
        assertEquals(itemAfterUpdate.getDescription(), updatetedItem.getDescription());
        assertEquals(itemAfterUpdate.getAvailable(), updatetedItem.getAvailable());
    }

    @Test
    void patchItemWrongUserId() {
        userController.create(testUser, request);

        int wrongOwnerId = 10;

        itemController.create(testItem, 1, request);

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> itemController.create(testItem, wrongOwnerId, request)
        );

        assertEquals("User with ID = " + wrongOwnerId + " was not found.", exception.getMessage());
    }

    @Test
    void testGetItemById() {
        userController.create(testUser, request);
        ItemDto testItem2 = new ItemDto(
                2,
                "Насос",
                "Насос обыкновенный",
                true,
                null
        );
        itemController.create(testItem, 1, request);
        ItemDto item2 = itemController.create(testItem2, 1, request);
        itemController.getItemById(item2.getId(), request);

        assertEquals(2, item2.getId());
    }

    @Test
    public void testSearchItem() {
        String text = "ОтВер";
        userController.create(testUser, request);
        ItemDto testItem2 = new ItemDto(
                2,
                "Отвертка",
                "Отвертка обыкновенная",
                true,
                null
        );
        itemController.create(testItem, 1, request);
        itemController.create(testItem2, 1, request);

        List<ItemDto> foundItems = new ArrayList<>(itemController.getItemsBySearchQuery(text, request));

        assertEquals(foundItems.size(), 1);
    }
}
