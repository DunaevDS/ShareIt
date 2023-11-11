package ru.practicum.shareit.exception;

public class InternalServerErrorException extends IllegalArgumentException {
    public InternalServerErrorException(String message) {
        super(message);
    }
}
