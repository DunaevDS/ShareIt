package ru.practicum.shareit.exception;

public class IncorrectDateException extends IllegalArgumentException {
    public IncorrectDateException(String message) {
        super(message);
    }
}
