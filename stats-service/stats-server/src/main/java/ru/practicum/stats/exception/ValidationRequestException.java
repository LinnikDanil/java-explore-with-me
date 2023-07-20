package ru.practicum.stats.exception;

public class ValidationRequestException extends RuntimeException {
    public ValidationRequestException(String message) {
        super(message);
    }
}
