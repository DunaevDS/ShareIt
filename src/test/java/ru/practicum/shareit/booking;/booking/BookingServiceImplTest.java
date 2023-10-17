package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.coment.CommentRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;


import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;


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

        private final UserDto user = new UserDto(1, "Apollon", "bestJavaProgrammer@yandex.ru");

        @BeforeEach
        void beforeEach() {
            bookingService = new BookingServiceImpl(mockBookingRepository,
                    mockUserService, mockItemRepository, mockCommentRepository);
        }

        @Test
        void test_GetBookingById_BookingNotFound() {
            Integer bookingId = 1;
            Integer userId = 1;

            when(mockUserService.findUserById(userId)).thenReturn(user);

            when(mockBookingRepository.findById(bookingId)).thenReturn(Optional.empty());

            assertThrows(BookingNotFoundException.class, () -> bookingService.getBookingById(bookingId, userId));
        }
    }
