package ru.practicum.shareit.handler;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorResponseTest {

    @Test
    void testErrorResponse() {
        String error = "Test Error";
        ErrorResponse response = new ErrorResponse(error);

        assertEquals(error, response.getError());
    }
}
