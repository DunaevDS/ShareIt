package ru.practicum.shareit.exception;

public class PaginationException extends IllegalArgumentException {
    public PaginationException(String message) {
        super(message);
    }
}
