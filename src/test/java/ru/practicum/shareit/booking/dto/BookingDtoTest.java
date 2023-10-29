package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoTest {
    private final JacksonTester<BookingDto> json;

    public BookingDtoTest(@Autowired JacksonTester<BookingDto> json) {
        this.json = json;
    }

    @Test
    void test_JsonBookingDto() throws Exception {
        BookingDto bookingDto = new BookingDto(
                1,
                LocalDateTime.of(2030, 12, 25, 12, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0),
                new ItemDto(1, "FirstItem", "DescriptionOfFirstItem", true,
                        new User(1, "FirstUser", "first@email.ru"), null, null,
                        null, null),
                new UserDto(2, "SecondUser", "second@email.ru"), Status.WAITING);

        JsonContent<BookingDto> result = json.write(bookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2030-12-25T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2030-12-26T12:00:00");
        assertThat(result).extractingJsonPathValue("$.requestId").isEqualTo(null);
        assertThat(result).extractingJsonPathStringValue("$.status").isEqualTo("WAITING");
    }
}