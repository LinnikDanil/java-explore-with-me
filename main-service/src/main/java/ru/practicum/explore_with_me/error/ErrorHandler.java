package ru.practicum.explore_with_me.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explore_with_me.category.controller.CategoryAdminController;
import ru.practicum.explore_with_me.category.controller.CategoryPublicController;
import ru.practicum.explore_with_me.comipation.controller.CompilationAdminController;
import ru.practicum.explore_with_me.comipation.controller.CompilationPublicController;
import ru.practicum.explore_with_me.event.controller.EventAdminController;
import ru.practicum.explore_with_me.event.controller.EventPrivateController;
import ru.practicum.explore_with_me.event.controller.EventPublicController;
import ru.practicum.explore_with_me.request.controller.RequestPrivateController;
import ru.practicum.explore_with_me.user.controller.UserAdminController;
import ru.practicum.explore_with_me.user.exception.UserNotFoundException;

@RestControllerAdvice(assignableTypes = {
        CategoryPublicController.class,
        CategoryAdminController.class,
        CompilationAdminController.class,
        CompilationPublicController.class,
        EventAdminController.class,
        EventPublicController.class,
        EventPrivateController.class,
        RequestPrivateController.class,
        UserAdminController.class
})
@Slf4j
public class ErrorHandler {
    //Users
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerUserNotFoundException(final UserNotFoundException e) {
        log.error("Received status 404 Not Found {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    //OTHER
    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerIllegalArgumentException(final IllegalArgumentException e) {
        log.error("Received status 400 Bad Request {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerMissingRequestHeaderException(final MissingRequestHeaderException e) {
        log.error("Received status 400 Bad Request {}", e.getMessage(), e);
        return new ErrorResponse("Заголовок отсутствует: " + e.getHeaderName());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Received status 400 Bad Request {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handlerException(final Exception e) {
        log.error("Received status 500 Internal Server {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }
}