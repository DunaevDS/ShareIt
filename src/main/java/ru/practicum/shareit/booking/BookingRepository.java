package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends PagingAndSortingRepository<Booking, Integer> {
    Page<Booking> findByBookerId(Integer bookerId, Pageable pageable);

    Optional<Booking> findByIdAndItem_Owner_Id(Integer itemId, Integer bookerId);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Integer bookerId, LocalDateTime start,
                                                              LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndEndIsBefore(Integer bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByBookerIdAndStartIsAfter(Integer bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByBookerIdAndStatus(Integer bookerId, Status status, Pageable pageable);

    Page<Booking> findByItem_Owner_Id(Integer ownerId, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Integer ownerId, LocalDateTime start,
                                                                   LocalDateTime end, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndEndIsBefore(Integer bookerId, LocalDateTime end, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndStartIsAfter(Integer bookerId, LocalDateTime start, Pageable pageable);

    Page<Booking> findByItem_Owner_IdAndStatus(Integer bookerId, Status status, Pageable pageable);

    Booking findFirstByItem_IdAndStartBeforeAndStatusNotOrderByStartDesc(Integer itemId, LocalDateTime time,
                                                                         Status status);

    Booking findFirstByItem_IdAndStartAfterAndStatusNotOrderByStart(Integer itemId, LocalDateTime start,
                                                                    Status status);

    Booking findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(Integer itemId, Integer userId,
                                                                  LocalDateTime end, Status status);
}
