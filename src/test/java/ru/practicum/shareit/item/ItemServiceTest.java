package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.item.coment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static java.lang.Thread.sleep;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final User user = new User(30, "First", "first@first30.ru");
    private final UserDto userDto1 = new UserDto(301, "AlexOne", "alexone@alex30.ru");
    private final UserDto userDto2 = new UserDto(302, "AlexTwo", "alextwo@alex30.ru");
    private final ItemDto itemDto = new ItemDto(301, "Item1", "Description1", true,
            user, null, null, null, null);
    private final ItemDto itemDto2 = new ItemDto(302, "Item2", "Description2", true,
            user, null, null, null, null);

    @Test
    void test_CreateItem() {
        UserDto newUserDto = userService.create(userDto1);
        ItemDto newItemDto = itemService.create(itemDto, newUserDto.getId());
        ItemDto returnItemDto = itemService.getItemById(newItemDto.getId(), newUserDto.getId());
        assertThat(returnItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void test_DeleteItem_UserNotOwner() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto, ownerDto.getId());
        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> itemService.delete(newItemDto.getId(), newUserDto.getId()));
        assertEquals("User with id= " + newUserDto.getId() + " dont have item with id= " + newItemDto.getId(),
                exp.getMessage());
    }

    @Test
    void test_DeleteItem_UserIsOwner() {
        UserDto ownerDto = userService.create(userDto1);
        ItemDto newItemDto = itemService.create(itemDto, ownerDto.getId());
        itemService.delete(newItemDto.getId(), ownerDto.getId());
        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> itemService.getItemById(newItemDto.getId(), ownerDto.getId()));
        assertEquals("NotFoundException: Item with id= " + newItemDto.getId() + " was not found.",
                exp.getMessage());
    }

    @Test
    void test_ExceptionWhenDeleteItemNotExist() {
        UserDto ownerDto = userService.create(userDto1);
        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> itemService.delete(-2, ownerDto.getId()));
        assertEquals("NotFoundException: Item with id= -2 was not found.", exp.getMessage());
    }

    @Test
    void test_UpdateItem() {
        UserDto newUserDto = userService.create(userDto1);
        ItemDto newItemDto = itemService.create(itemDto, newUserDto.getId());
        newItemDto.setName("NewName");
        newItemDto.setDescription("NewDescription");
        newItemDto.setAvailable(false);
        ItemDto returnItemDto = itemService.update(newItemDto, newUserDto.getId());
        assertThat(returnItemDto.getName(), equalTo("NewName"));
        assertThat(returnItemDto.getDescription(), equalTo("NewDescription"));
        assertFalse(returnItemDto.getAvailable());
    }

    @Test
    void test_ExceptionWhenUpdateItemNotOwner() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto, ownerDto.getId());
        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> itemService.update(newItemDto, newUserDto.getId()));
        assertEquals("NotFoundException: Item with id= " + newItemDto.getId() + " was not found.",
                exp.getMessage());
    }

    @Test
    void test_ReturnItemsByOwner() {
        UserDto ownerDto = userService.create(userDto1);
        itemService.create(itemDto, ownerDto.getId());
        itemService.create(itemDto2, ownerDto.getId());
        List<ItemDto> listItems = itemService.getItemsByOwner(ownerDto.getId(), 0, 10);
        assertEquals(2, listItems.size());
    }

    @Test
    void test_ReturnItemsByOwner_SizeNull() {
        UserDto ownerDto = userService.create(userDto1);
        itemService.create(itemDto, ownerDto.getId());
        itemService.create(itemDto2, ownerDto.getId());
        assertThrows(BadRequestException.class,
                () -> itemService.getItemsByOwner(ownerDto.getId(), 0, -1));
    }

    @Test
    void test_ReturnItemsBySearch() {
        UserDto ownerDto = userService.create(userDto1);
        itemService.create(itemDto, ownerDto.getId());
        itemService.create(itemDto2, ownerDto.getId());
        List<ItemDto> listItems = itemService.getItemsBySearchQuery("item", 0, 1);
        assertEquals(1, listItems.size());
    }

    @Test
    void test_ExceptionWhenCreateComment_UserNotBooker() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        CommentDto commentDto = new CommentDto(
                1,
                "Comment1",
                ItemMapper.mapToItem(itemDto, UserMapper.mapToUser(ownerDto)),
                newUserDto.getName(),
                LocalDateTime.now()
        );
        BadRequestException exp = assertThrows(BadRequestException.class,
                () -> itemService.createComment(commentDto.getText(), itemDto.getId(), newUserDto.getId()));
        assertEquals("User with id= " + newUserDto.getId() + " did not book item with id= " + itemDto.getId(),
                exp.getMessage());
    }

    @Test
    void test_CreateComment() {
        UserDto ownerDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);
        ItemDto newItemDto = itemService.create(itemDto, ownerDto.getId());
        PostBookingDto bookingInputDto = new PostBookingDto(
                newItemDto.getId(),
                LocalDateTime.now().plusSeconds(1),
                LocalDateTime.now().plusSeconds(3)
        );
        BookingDto bookingDto = bookingService.create(bookingInputDto, newUserDto.getId());
        bookingService.update(bookingDto.getId(), ownerDto.getId(), true);
        try {
            sleep(5000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        CommentDto commentDto = new CommentDto(
                1,
                "Comment1",
                ItemMapper.mapToItem(itemDto, UserMapper.mapToUser(ownerDto)),
                newUserDto.getName(),
                LocalDateTime.now()
        );
        itemService.createComment(commentDto.getText(), newItemDto.getId(), newUserDto.getId());
        Assertions.assertEquals(1, itemService.getCommentsByItemId(newItemDto.getId()).size());
    }
}