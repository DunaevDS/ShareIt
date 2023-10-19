package ru.practicum.shareit.requests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;

import ru.practicum.shareit.item.ItemService;

import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.ItemRequestServiceImpl;

import ru.practicum.shareit.user.UserService;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository mockItemRequestRepository;
    @Mock
    private UserService mockUserService;
    @Mock
    private ItemService mockItemService;

    private ItemRequestService itemRequestService;
    private final UserDto user = new UserDto(1, "Apollon", "bestJavaProgrammer@yandex.ru");

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(mockItemRequestRepository, mockUserService, mockItemService);
    }


    @Test
    void test_GetItemRequestById_ItemRequestNotFound() {
        Integer itemRequestId = 1;
        Integer userId = 1;

        when(mockUserService.findUserById(userId)).thenReturn(user);

        when(mockItemRequestRepository.findById(itemRequestId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                itemRequestService.getItemRequestById(itemRequestId, userId));
    }
}