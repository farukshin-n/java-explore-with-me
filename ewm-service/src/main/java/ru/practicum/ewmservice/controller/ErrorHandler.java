package ru.practicum.ewmservice.controller;

import io.micrometer.core.instrument.config.validate.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewmservice.exception.*;

import javax.persistence.EntityNotFoundException;
import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadRequestException(BadRequestException e) {
        log.error(e.getMessage());

        return new ApiError(
                List.of("Bad request exception"),
                e.getMessage(),
                "Check your request, it's very, very bad.",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(BadStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleBadStateException(BadStateException e) {
        log.error(e.getMessage());

        return new ApiError(
                List.of("Bad EventState exception"),
                e.getMessage(),
                "Problem with event state",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(DateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleDateException(DateException e) {
        log.error(e.getMessage());

        return new ApiError(
                List.of("Date exception"),
                e.getMessage(),
                "Error with date",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiError handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage());

        return new ApiError(
                List.of("Http message not readable"),
                e.getMessage(),
                "Incorrect data was sent in the request",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidationException(final ValidationException e) {
        log.error(e.getMessage());

        return new ApiError(
                List.of("ValidationException"),
                e.getMessage(),
                "Incorrect request.",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(ValidateConflictException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidateException(ValidateConflictException e) {
        log.error(e.getMessage());

        return new ApiError(
                List.of("Validation exception"),
                e.getMessage(),
                "Problem with validation",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log.error(e.getMessage());
        List<String> errors = e.getBindingResult().getFieldErrors().stream()
                .peek(ex -> log.info("Validation error: {}", ex.getDefaultMessage()))
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList());

        return new ApiError(
                errors,
                e.getMessage(),
                "Incorrect data was sent in the request",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingServletRequestParameterException(final MissingServletRequestParameterException e) {
        log.error(e.getMessage());

        return new ApiError(
                List.of("Missing Servlet RequestParameter Exception"),
                e.getMessage(),
                "Incorrect request.",
                HttpStatus.BAD_REQUEST,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotAvailableCases(EntityNotFoundException e) {
        log.error(e.getMessage());

        return new ApiError(
                List.of("EntityNotFoundException"),
                e.getMessage(),
                "Error with entity",
                HttpStatus.NOT_FOUND,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(ForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ApiError handleForbiddenExceptions(ForbiddenException e) {
        log.error(e.getMessage());

        return new ApiError(
                List.of("ForbiddenException"),
                e.getMessage(),
                "This action is forbidden.",
                HttpStatus.FORBIDDEN,
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiError handleMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        log.error(e.getMessage());

        return new ApiError(
                List.of("Method not allowed exception"),
                e.getMessage(),
                "The method used in the request is not allowed by this endpoint",
                HttpStatus.METHOD_NOT_ALLOWED,
                LocalDateTime.now()
        );
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ApiError handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.error(e.getMessage());

        return new ApiError(
                List.of("Data integrity violation"),
                e.getMessage(),
                "Problem with data integrity",
                HttpStatus.CONFLICT,
                LocalDateTime.now()
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiError handleConstraintViolationException(final ConstraintViolationException e) {
        log.error(e.getMessage());

        List<String> errors = e.getConstraintViolations().stream()
                .peek(ex -> log.info("Constraint violation error: {}", ex.getMessage()))
                .map(v -> v.getPropertyPath().toString())
                .collect(Collectors.toList());

        return new ApiError(
                errors,
                e.getMessage(),
                "Constraint violation.",
                HttpStatus.CONFLICT,
                LocalDateTime.now());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(RuntimeException.class)
    public ApiError handleServerErrors(RuntimeException e) {
        log.error(e.getMessage());

        return new ApiError(
                List.of("RuntimeException"),
                e.getMessage(),
                "Internal server error has happened.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                LocalDateTime.now()
        );
    }
}
