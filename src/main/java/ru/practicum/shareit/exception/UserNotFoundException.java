package ru.practicum.shareit.exception;

public class UserNotFoundException extends IllegalArgumentException {
    public UserNotFoundException(String message) {
        super(message);
    }
}
