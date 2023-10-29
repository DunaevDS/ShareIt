package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends PagingAndSortingRepository<ItemRequest, Integer> {
    List<ItemRequest> findAllByrequesterId(Integer requesterId, Sort sort);

    Page<ItemRequest> findAllByrequesterIdNot(Integer userId, Pageable pageable);

    List<ItemRequest> findAllByrequesterIdNotOrderByCreatedDesc(Integer userId);
}
