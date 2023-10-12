package ru.practicum.shareit.exception;

public class PermissionException extends IllegalArgumentException {
    public PermissionException(String message) {
        super(message);
    }
}