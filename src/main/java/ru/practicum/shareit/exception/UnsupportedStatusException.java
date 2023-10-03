package ru.practicum.shareit.exception;

public class UnsupportedStatusException extends IllegalArgumentException {
    public UnsupportedStatusException(String message) {
        super(message);
    }
}
