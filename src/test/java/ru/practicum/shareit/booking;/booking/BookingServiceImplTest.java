package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingAlreadyApprovedException;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.IncorrectDateException;
import ru.practicum.shareit.exception.PermissionException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.coment.CommentRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;


    @SpringBootTest
    @ExtendWith(MockitoExtension.class)
    public class BookingServiceImplTest {
        @Mock
        private BookingRepository mockBookingRepository;
        @Mock
        private UserService mockUserService;
        @Mock
        private ItemRepository mockItemRepository;
        @Mock
        private CommentRepository mockCommentRepository;

        private BookingService bookingService;

        @BeforeEach
        void beforeEach() {
            bookingService = new BookingServiceImpl(mockBookingRepository,
                    mockUserService, mockItemRepository, mockCommentRepository);
        }

        @Test
        void test_GetBookingById_BookingNotFound() {
            Integer bookingId = 1;
            Integer userId = 1;

            when(mockUserService.findUserById(userId)).thenReturn(
                    new UserDto(1, "Apollon", "apollo@yandex.ru"));

            when(mockBookingRepository.findById(bookingId)).thenReturn(Optional.empty());

            assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));
        }

        /*@Test
        void update_ThrowsValidationException_WhenBookingIsCancelled() {
            Integer bookingId = 1;
            Integer userId = 1;
            Integer ownerId = 2;
            Boolean approved = true;
            User user = new User(userId, "Apollon", "apollo@yandex.ru");
            User owner = new User(ownerId, "owner", "owner@yandex.ru");
            Item item = new Item(1, "Item", "Description", true, owner, null);
            Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, user,
                    Status.CANCELED);
            when(mockUserService.findUserById(userId)).thenReturn(UserMapper.mapToUserDto(user));
            when(mockBookingRepository.findByIdAndItem_Owner_Id(bookingId, userId)).thenReturn(Optional.of(booking));

            assertThrows(ValidationException.class, () -> bookingService.update(bookingId, userId, approved));

            verify(mockBookingRepository, never()).save(any(Booking.class));
        }*/

        @Test
        void update_BookingAlreadyApprovedException() {
            Integer bookingId = 1;
            Integer userId = 2;
            Boolean approved = true;
            Item item = new Item();
            item.setId(1);
            Booking booking = new Booking();
            booking.setId(bookingId);
            booking.setStatus(Status.APPROVED);
            booking.setItem(item);
            when(mockUserService.findUserById(userId)).thenReturn(new UserDto(userId, "Apollon", "apollo@yandex.ru"));
            when(mockBookingRepository.findByIdAndItem_Owner_Id(any(), any())).thenReturn(Optional.of(booking));
            assertThrows(IncorrectDateException.class, () -> bookingService.update(bookingId, userId, approved));
        }

        /*@Test
        void update_ThrowsPermissionException_WhenBookingIsRejectedByNonOwner() {
            Integer bookingId = 1;
            Integer userId = 1;
            Integer ownerId = 2;
            Boolean approved = true;
            User user = new User(userId, "Apollon", "apollo@yandex.ru");
            User owner = new User(ownerId, "owner", "owner@yandex.ru");
            Item item = new Item(1, "Item", "Description", true, owner, null);
            Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, user,
                    Status.WAITING);
            when(mockUserService.findUserById(userId)).thenReturn(UserMapper.mapToUserDto(user));
            when(mockBookingRepository.findByIdAndItem_Owner_Id(bookingId, userId)).thenReturn(Optional.of(booking));

            assertThrows(PermissionException.class, () -> bookingService.update(bookingId, userId, approved));

            verify(mockBookingRepository, never()).save(any(Booking.class));
        }*/

        /*@Test
        void update_ThrowsBookingAlreadyApprovedException_WhenBookingStatusIsNotWaiting() {
            Integer bookingId = 1;
            Integer userId = 1;
            Integer ownerId = 2;
            Integer itemId = 1;
            Boolean approved = true;
            User user = new User(userId, "user", "apollo@yandex.ru");
            User owner = new User(ownerId, "owner", "owner@yandex.ru");
            Item item = new Item(itemId, "Item", "Description", true, owner, null);
            Booking booking = new Booking(bookingId, LocalDateTime.now(), LocalDateTime.now().plusHours(1), item, user,
                    Status.APPROVED);

            when(mockItemRepository.findByOwnerId(userId, Pageable.unpaged())).thenReturn(
                    Collections.singletonList(ItemMapper.mapToItemDto(item, Collections.emptyList()))
            );

            when(mockUserService.findUserById(userId)).thenReturn(UserMapper.mapToUserDto(user));
            when(mockBookingRepository.findByIdAndItem_Owner_Id(bookingId, userId)).thenReturn(Optional.of(booking));

            assertThrows(BookingAlreadyApprovedException.class, () -> bookingService.update(bookingId, userId, approved));

            verify(mockBookingRepository, never()).save(any(Booking.class));
        }*/
    }
