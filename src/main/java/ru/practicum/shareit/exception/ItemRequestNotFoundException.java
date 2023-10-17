package ru.practicum.shareit.exception;

public class ItemRequestNotFoundException extends IllegalArgumentException {
    public ItemRequestNotFoundException(String message) {
        super(message);
    }
}
