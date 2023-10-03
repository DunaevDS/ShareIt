package ru.practicum.shareit.exception;

public class BookingAlreadyApprovedException extends IllegalArgumentException {
    public BookingAlreadyApprovedException(String message) {
        super(message);
    }
}
