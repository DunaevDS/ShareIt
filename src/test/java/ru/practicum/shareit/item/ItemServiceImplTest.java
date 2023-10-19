package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.dto.PostBookingDto;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.coment.CommentRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {

    @Mock
    private ItemRepository mockItemRepository;

    @Mock
    private UserService mockUserService;

    @Mock
    private CommentRepository mockCommentRepository;

    @Mock
    private BookingService mockBookingService;
    @Mock
    private BookingRepository mockBookingRepository;


    @Test
    void test_ExceptionWhenGetItemWithWrongId() {
        ItemService itemService = new ItemServiceImpl(
                mockItemRepository,
                mockUserService,
                mockCommentRepository,
                mockBookingService
        );
        when(mockItemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());
        int itemId = -1;
        final ItemNotFoundException exception = assertThrows(
                ItemNotFoundException.class,
                () -> itemService.getItemById(itemId, 1));
        assertEquals("NotFoundException: Item with id= " + itemId + " was not found.",
                exception.getMessage());
    }

    @Test
    void create_ThrowsItemNotFoundException_WhenItemDtoIsNull() {
        ItemService itemService = new ItemServiceImpl(
                mockItemRepository,
                mockUserService,
                mockCommentRepository,
                mockBookingService
        );

        assertThrows(ItemNotFoundException.class, () -> itemService.create(null, 1));
    }

    @Test
    void create_ThrowsItemNotFoundException_WhenItemNotFound() {
        Integer bookerId = 1;
        Integer itemId = 1;


        PostBookingDto postBookingDto = new PostBookingDto(itemId, LocalDateTime.now(), LocalDateTime.now().plusHours(1));
        when(mockItemRepository.findById(itemId)).thenReturn(Optional.empty());
        when(mockUserService.findUserById(any(Integer.class))).thenReturn(UserMapper.mapToUserDto(
                new User(1, "Apollon", "bestJavaProgrammer@yandex.ru"))
        );

        BookingService bookingService = new BookingServiceImpl(
                mockBookingRepository,
                mockUserService,
                mockItemRepository,
                mockCommentRepository
        );

        assertThrows(ItemNotFoundException.class, () -> bookingService.create(postBookingDto, bookerId));
    }

    @Test
    void delete_ThrowsItemNotFoundException_WhenItemNotFound() {
        Integer itemId = 4;
        Integer ownerId = 1;
        lenient().doThrow(EmptyResultDataAccessException.class).when(mockItemRepository).deleteById(itemId);

        ItemService itemService = new ItemServiceImpl(
                mockItemRepository,
                mockUserService,
                mockCommentRepository,
                mockBookingService
        );

        assertThrows(ItemNotFoundException.class, () -> itemService.delete(itemId, ownerId));
    }
}