package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserCommentException;
import ru.practicum.shareit.item.coment.CommentMapper;
import ru.practicum.shareit.item.coment.CommentRepository;
import ru.practicum.shareit.item.coment.dto.CommentDto;
import ru.practicum.shareit.item.coment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final BookingService bookingService;


    @Autowired
    public ItemServiceImpl(ItemRepository itemRepository,
                           UserService userService,
                           ItemMapper itemMapper,
                           CommentMapper commentMapper,
                           CommentRepository commentRepository,
                           BookingService bookingService) {
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.itemMapper = itemMapper;
        this.commentMapper = commentMapper;
        this.commentRepository = commentRepository;
        this.bookingService = bookingService;
    }

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Integer ownerId) {
        if (itemDto == null) {
            log.error("EmptyObjectException: Item is null.");
            throw new ItemNotFoundException("Item was not provided");
        }
        userService.findUserById(ownerId);

        return itemMapper.mapToItemDto(
                itemRepository.save(itemMapper.mapToItem(itemDto, ownerId))
        );
    }

    @Override
    public List<ItemDto> getItemsByOwner(Integer ownerId) {
        userService.findUserById(ownerId);

        return itemRepository.findByOwnerId(ownerId).stream()
                .map(itemMapper::toItemWithBookingDto)
                .sorted(Comparator.comparing(ItemDto::getId))
                .collect(toList());
    }

    @Override
    public ItemDto getItemById(Integer itemId, Integer userId) {
        ItemDto itemDto;
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            log.error("NotFoundException: Item with id='{}' was not found.", itemId);
            throw new ItemNotFoundException("Item was not found");
        }
        if (userId.equals(item.getOwner().getId())) {
            itemDto = itemMapper.toItemWithBookingDto(item);
        } else {
            itemDto = itemMapper.mapToItemDto(item);
        }
        return itemDto;
    }

    @Override
    public Item findItemById(Integer itemId) {
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            log.error("NotFoundException: Item with id='{}' was not found.", itemId);
            throw new ItemNotFoundException("Item was not found");
        }
        return item;
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Integer ownerId) {
        userService.findUserById(ownerId);
        Item item = itemRepository.findById(itemDto.getId()).orElse(null);
        if (item == null) {
            log.error("NotFoundException: Item with id='{}' was not found.", itemDto.getId());
            throw new ItemNotFoundException("Item was not found");
        }
        if (!itemRepository.existsById(itemDto.getId())) {
            log.error("NotFoundException: Item with id='{}' was not found.", itemDto.getId());
            throw new ItemNotFoundException("Item was not found");
        }
        if (!item.getOwner().getId().equals(ownerId)) {
            log.error("NotFoundException: User with id='{}' dont have item with id='{}'", ownerId, itemDto.getId());
            throw new ItemNotFoundException("User with id= " + ownerId + " dont have item with id= " + itemDto.getId());
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return itemMapper.mapToItemDto(itemRepository.save(item));

    }

    @Override
    @Transactional
    public void delete(Integer itemId, Integer ownerId) {
        userService.findUserById(ownerId);
        Item item = itemRepository.findById(itemId).orElse(null);
        if (item == null) {
            log.error("NotFoundException: Item with id='{}' was not found.", itemId);
            throw new ItemNotFoundException("Item was not found");
        }
        if (!item.getOwner().getId().equals(ownerId)) {
            log.error("NotFoundException: User with id='{}' dont have item with id='{}'", ownerId, itemId);
            throw new ItemNotFoundException("User with id= " + ownerId + " dont have item with id= " + itemId);
        }
        try {
            itemRepository.deleteById(itemId);
        } catch (EmptyResultDataAccessException e) {
            log.error("NotFoundException: Item with id='{}' was not found.", itemId);
            throw new ItemNotFoundException("Item was not found");
        }
    }

    @Override
    public List<ItemDto> getItemsBySearchQuery(String text) {
        if ((text != null) && (!text.isEmpty()) && (!text.isBlank())) {
            text = text.toLowerCase();
            return itemRepository.getItemsBySearchQuery(text).stream()
                    .map(itemMapper::mapToItemDto)
                    .collect(toList());
        } else return new ArrayList<>();
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Integer itemId, Integer userId) {
        userService.findUserById(userId);
        Comment comment = new Comment();
        Booking booking = bookingService.getBookingWithUserBookedItem(itemId, userId);
        if (booking != null) {
            comment.setCreated(LocalDateTime.now());
            comment.setItem(booking.getItem());
            comment.setAuthor(booking.getBooker());
            comment.setText(commentDto.getText());
        } else {
            log.error("ValidationException: User with id='{}' did not book item with id='{}'", userId, itemId);
            throw new UserCommentException("User with id= " + userId + " did not book item with id= " + itemId);
        }
        return commentMapper.mapToCommentDto(commentRepository.save(comment));
    }

    @Override
    public List<CommentDto> getCommentsByItemId(Integer itemId) {
        return commentRepository.findAllByItem_Id(itemId,
                        Sort.by(Sort.Direction.DESC, "created")).stream()
                .map(commentMapper::mapToCommentDto)
                .collect(toList());
    }
}
