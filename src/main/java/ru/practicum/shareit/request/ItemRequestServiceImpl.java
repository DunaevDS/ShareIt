package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Pagination;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
@Transactional
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestRepository repository, UserService userService, ItemService itemService) {
        this.repository = repository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Integer requesterId, LocalDateTime created) {
        UserDto user = userService.findUserById(requesterId);

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto,
                UserMapper.mapToUser(user),
                created
        );

        List<ItemDto> itemsListByRequest = itemService.getItemsByRequestId(itemRequest.getId());

        return ItemRequestMapper.toItemRequestDto(repository.save(itemRequest), itemsListByRequest);
    }

    @Override
    public ItemRequestDto getItemRequestById(Integer itemRequestId, Integer userId) {
        userService.findUserById(userId);

        ItemRequest itemRequest = repository.findById(itemRequestId)
                .orElseThrow(() -> throwNotFoundException(
                        "NotFoundException: request with id=" + itemRequestId + " was not found."));

        List<ItemDto> itemsListByRequest = itemService.getItemsByRequestId(itemRequestId);

        return ItemRequestMapper.toItemRequestDto(itemRequest, itemsListByRequest);
    }

    @Override
    public List<ItemRequestDto> getOwnItemRequests(Integer requesterId) {
        userService.findUserById(requesterId);

        return repository.findAllByrequesterId(requesterId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(itemRequest -> {
                    Integer id = itemRequest.getId();
                    List<ItemDto> itemsListByRequest = itemService.getItemsByRequestId(id);
                    return ItemRequestMapper.toItemRequestDto(itemRequest, itemsListByRequest);
                })
                .collect(toList());
    }

    @Override
    public List<ItemRequestDto> getAllItemRequests(Integer userId, Integer from, Integer size) {
        userService.findUserById(userId);

        List<ItemRequestDto> listItemRequestDto = new ArrayList<>();
        Pageable pageable;
        Page<ItemRequest> page;
        Pagination pager = new Pagination(from, size);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");

        if (size == null) {
            List<ItemRequest> listItemRequest = repository.findAllByrequesterIdNotOrderByCreatedDesc(userId);
            listItemRequestDto
                    .addAll(listItemRequest.stream()
                            .skip(from)
                            .map(itemRequest -> {
                                Integer id = itemRequest.getId();
                                List<ItemDto> itemsListByRequest = itemService.getItemsByRequestId(id);
                                return ItemRequestMapper.toItemRequestDto(itemRequest, itemsListByRequest);
                            })
                            .collect(toList()));
        } else {
            for (int i = pager.getIndex(); i < pager.getTotalPages(); i++) {
                pageable =
                        PageRequest.of(i, pager.getPageSize(), sort);
                page = repository.findAllByrequesterIdNot(userId, pageable);
                listItemRequestDto.addAll(page.stream()
                        .map(itemRequest -> {
                            Integer id = itemRequest.getId();
                            List<ItemDto> itemsListByRequest = itemService.getItemsByRequestId(id);
                            return ItemRequestMapper.toItemRequestDto(itemRequest, itemsListByRequest);
                        })
                        .collect(toList()));
                if (!page.hasNext()) {
                    break;
                }
            }
            listItemRequestDto = listItemRequestDto.stream()
                    .limit(size)
                    .collect(toList());
        }
        return listItemRequestDto;
    }

    private NotFoundException throwNotFoundException(String message) {
        log.error(message);
        throw new NotFoundException(message);
    }
}
