package ru.practicum.ewmservice.exception;

public class StatsErrorException extends RuntimeException {
    public StatsErrorException(String message) {
        super(message);
    }
}
