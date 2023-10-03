package ru.practicum.shareit.exception;

public class ItemNotFoundException extends IllegalArgumentException {
    public ItemNotFoundException(String message) {
        super(message);
    }
}
