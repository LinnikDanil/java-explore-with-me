package ru.practicum.explore_with_me.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.explore_with_me.category.controller.CategoryAdminController;
import ru.practicum.explore_with_me.category.controller.CategoryPublicController;
import ru.practicum.explore_with_me.compilation.controller.CompilationAdminController;
import ru.practicum.explore_with_me.compilation.controller.CompilationPublicController;
import ru.practicum.explore_with_me.error.exception.*;
import ru.practicum.explore_with_me.event.controller.EventAdminController;
import ru.practicum.explore_with_me.event.controller.EventPrivateController;
import ru.practicum.explore_with_me.event.controller.EventPublicController;
import ru.practicum.explore_with_me.request.controller.RequestPrivateController;
import ru.practicum.explore_with_me.user.controller.UserAdminController;

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
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundEwmException(final NotFoundEwmException e) {
        log.error("Received status 404 Not Found {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerAlreadyExistEwmException(final AlreadyExistEwmException e) {
        log.error("Received status 409 Conflict {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handlerForbiddenActionEwmException(final ForbiddenActionEwmException e) {
        log.error("Received status 409 Conflict {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerGettingStatisticsEwmException(final GettingStatisticsEwmException e) {
        log.error("Received status 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlerValidationEwmException(final ValidationEwmException e) {
        log.error("Received status 400 Bad request {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

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

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMissingServletRequestParameterException(
        final MissingServletRequestParameterException e) {
        log.error("Received status 400 Bad Request {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        log.error("Received status 400 Bad Request {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
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