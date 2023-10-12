package ru.practicum.shareit.exception;

public class BookingNotFoundException extends IllegalArgumentException {
    public BookingNotFoundException(String message) {
        super(message);
    }
}