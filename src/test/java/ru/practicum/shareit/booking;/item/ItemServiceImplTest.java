package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.ItemNotFoundException;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository mockItemRepository;

    @Test
    void test_ExceptionWhenGetItemWithWrongId() {
        ItemService itemService = new ItemServiceImpl(
                mockItemRepository,
                null,
                null,
                null
        );

        when(mockItemRepository.findById(any(Integer.class)))
                .thenReturn(Optional.empty());

        int itemId = -1;

        final ItemNotFoundException exception = Assertions.assertThrows(
                ItemNotFoundException.class,
                () -> itemService.getItemById(itemId, 1));
        Assertions.assertEquals("NotFoundException: Item with id= " + itemId + " was not found.",
                exception.getMessage());
    }
}

