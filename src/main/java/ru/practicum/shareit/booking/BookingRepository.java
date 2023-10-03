package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Integer> {
    List<Booking> findByBookerId(Integer bookerId, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Integer bookerId, LocalDateTime start,
                                                              LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndEndIsBefore(Integer bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Integer bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByBookerIdAndStatus(Integer bookerId, Status status, Sort sort);

    List<Booking> findByItem_Owner_Id(Integer ownerId, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartIsBeforeAndEndIsAfter(Integer ownerId, LocalDateTime start,
                                                                   LocalDateTime end, Sort sort);

    List<Booking> findByItem_Owner_IdAndEndIsBefore(Integer bookerId, LocalDateTime end, Sort sort);

    List<Booking> findByItem_Owner_IdAndStartIsAfter(Integer bookerId, LocalDateTime start, Sort sort);

    List<Booking> findByItem_Owner_IdAndStatus(Integer bookerId, Status status, Sort sort);

    Booking findFirstByItem_IdAndStartBeforeAndStatusNotOrderByStartDesc(Integer itemId, LocalDateTime time,
                                                                     Status status);

    Booking findFirstByItem_IdAndStartAfterAndStatusNotOrderByStart(Integer itemId, LocalDateTime start,
                                                                       Status status);

    Booking findFirstByItem_IdAndBooker_IdAndEndIsBeforeAndStatus(Integer itemId, Integer userId,
                                                                  LocalDateTime end, Status status);
}
