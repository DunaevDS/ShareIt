package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.json.JsonTest;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class PostBookingDtoTest {
    private PostBookingDto postBookingDto;


    @BeforeEach
    void beforeEach() {
        postBookingDto = new PostBookingDto(
                1,
                LocalDateTime.of(2030, 12, 25, 12, 0),
                LocalDateTime.of(2030, 12, 26, 12, 0)
        );
    }

    @Test
    void test_JsonBookingInputDto() {
        assertThat(postBookingDto.getItemId()).isEqualTo(1);
        assertThat(postBookingDto.getStart()).isEqualTo(LocalDateTime.of(2030, 12, 25, 12, 0));
        assertThat(postBookingDto.getEnd()).isEqualTo(LocalDateTime.of(2030, 12, 26, 12, 0));
    }
}
