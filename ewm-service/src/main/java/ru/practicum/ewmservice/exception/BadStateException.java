package ru.practicum.ewmservice.exception;

public class BadStateException extends RuntimeException {
    public BadStateException(String message) {
        super(message);
    }
}
