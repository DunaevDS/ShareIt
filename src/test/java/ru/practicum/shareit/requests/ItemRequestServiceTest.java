package ru.practicum.shareit.requests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.coment.CommentRepository;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
public class ItemRequestServiceTest {
    ItemRequestService itemRequestService;
    @Autowired
    UserService userService;
    @Autowired
    ItemRequestRepository itemRequestRepository;
    @Autowired
    ItemService itemService;
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    CommentRepository commentRepository;
    private final UserDto userDto1 = new UserDto(1, "User1", "user1@yandex.ru");
    private final UserDto userDto2 = new UserDto(2, "User2", "user2@yandex.ru");

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1, "Description",
            userDto1, LocalDateTime.of(2023, 1, 2, 3, 4, 5), null);

    @BeforeEach
    void beforeEach() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository,
                userService, itemRepository, commentRepository);
    }

    @Test
    void test_CreateItemRequest() {
        UserDto newUserDto = userService.create(userDto1);
        ItemRequestDto returnRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId()
        );
        assertThat(returnRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
    }

    @Test
    void test_ExceptionWhenCreateItemRequestWithWrongUserId() {
        int requesterId = -2;
        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> itemRequestService.create(itemRequestDto, requesterId
                ));
        assertEquals("NotFoundException: User with id= " + requesterId + " was not found.", exp.getMessage());
    }

    @Test
    void test_ExceptionWhenGetItemRequestWithWrongId() {
        int itemRequestId = -2;
        UserDto firstUserDto = userService.create(userDto1);
        NotFoundException exp = assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(itemRequestId, firstUserDto.getId()));
        assertEquals("NotFoundException: request with id=" + itemRequestId + " was not found.",
                exp.getMessage());
    }

    @Test
    void test_ReturnAllItemRequestsWhenSizeNotNull() {
        UserDto firstUserDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);

        ItemRequestDto returnOneRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId()
        );
        ItemRequestDto returnTwoRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId()
        );

        List<ItemRequestDto> listItemRequest = itemRequestService.getAllItemRequests(firstUserDto.getId(),
                0, 10);

        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void test_ReturnAllItemRequestsWhenSizeNull() {
        UserDto firstUserDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);

        ItemRequestDto returnOneRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId()
        );
        ItemRequestDto returnTwoRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId()
        );

        List<ItemRequestDto> listItemRequest = itemRequestService.getAllItemRequests(firstUserDto.getId(),
                0, null);

        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void test_ReturnOwnItemRequests() {
        UserDto firstUserDto = userService.create(userDto1);
        UserDto newUserDto = userService.create(userDto2);

        ItemRequestDto returnOneRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId()
        );
        ItemRequestDto returnTwoRequestDto = itemRequestService.create(itemRequestDto, newUserDto.getId()
        );

        List<ItemRequestDto> listItemRequest = itemRequestService.getOwnItemRequests(newUserDto.getId());

        assertThat(listItemRequest.size(), equalTo(2));
    }

    @Test
    void test_ReturnItemRequestById() {
        UserDto firstUserDto = userService.create(userDto1);

        ItemRequestDto newItemRequestDto = itemRequestService.create(itemRequestDto, firstUserDto.getId()
        );
        ItemRequestDto returnItemRequestDto = itemRequestService.getItemRequestById(newItemRequestDto.getId(),
                firstUserDto.getId());

        assertThat(returnItemRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
    }
}