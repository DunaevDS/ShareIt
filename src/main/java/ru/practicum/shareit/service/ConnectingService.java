package ru.practicum.shareit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.ItemServiceImpl;
import ru.practicum.shareit.item.coment.dto.CommentDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.List;

//помечать как @Сервис или как @Компонент ?
@Service
public class ConnectingService {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;

    @Autowired
    public ConnectingService(UserServiceImpl userService,
                             ItemServiceImpl itemService,
                             BookingService bookingService) {
        this.userService = userService;
        this.itemService = itemService;
        this.bookingService = bookingService;
    }

    public User findUserById(Integer userId) {
        return userService.findUserById(userId);
    }

    public BookingShortDto getLastBooking(Integer itemId) {
        return bookingService.getLastBooking(itemId);
    }

    public BookingShortDto getNextBooking(Integer itemId) {
        return bookingService.getNextBooking(itemId);
    }

    public List<CommentDto> getCommentsByItemId(Integer itemId) {
        return itemService.getCommentsByItemId(itemId);
    }
}