package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class PostBookingDtoTest {
    private final JacksonTester<PostBookingDto> json;
    private PostBookingDto postBookingDto;
    private final Validator validator;

    public PostBookingDtoTest(@Autowired JacksonTester<PostBookingDto> json) {
        this.json = json;
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @BeforeEach
    void beforeEach() {
        postBookingDto = new PostBookingDto(
                1,
                LocalDateTime.of(2030,12,25,12,0),
                LocalDateTime.of(2030,12,26,12,0)
        );
    }

    @Test
    void test_JsonBookingInputDto() throws Exception {
        JsonContent<PostBookingDto> result = json.write(postBookingDto);
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo("2030-12-25T12:00:00");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo("2030-12-26T12:00:00");
    }

    @Test
    void test_BookingInputDtoIsValid() {
        Set<ConstraintViolation<PostBookingDto>> violations = validator.validate(postBookingDto);
        assertThat(violations).isEmpty();
    }

    @Test
    void test_BookingInputDtoItemIdNotNull() {
        postBookingDto.setItemId(null);
        Set<ConstraintViolation<PostBookingDto>> violations = validator.validate(postBookingDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно равняться null'");
    }

    @Test
    void test_BookingInputDtoStartNotNull() {
        postBookingDto.setStart(null);
        Set<ConstraintViolation<PostBookingDto>> violations = validator.validate(postBookingDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно равняться null'");
    }

    @Test
    void test_BookingInputDtoEndNotNull() {
        postBookingDto.setEnd(null);
        Set<ConstraintViolation<PostBookingDto>> violations = validator.validate(postBookingDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='не должно равняться null'");
    }

    @Test
    void test_BookingInputDtoStartBeforeNow() {
        postBookingDto.setStart(LocalDateTime.now().minusSeconds(1));
        Set<ConstraintViolation<PostBookingDto>> violations = validator.validate(postBookingDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='должно содержать" +
                " сегодняшнее число или дату, которая еще не наступила'");
    }

    @Test
    void test_BookingInputDtoEndBeforeNow() {
        postBookingDto.setEnd(LocalDateTime.now().minusSeconds(1));
        Set<ConstraintViolation<PostBookingDto>> violations = validator.validate(postBookingDto);
        assertThat(violations).isNotEmpty();
        assertThat(violations.toString()).contains("interpolatedMessage='должно содержать дату," +
                " которая еще не наступила'");
    }
}
