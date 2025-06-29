package com.task.managment.web;

import com.task.management.application.shared.UseCaseException;
import com.task.managment.web.shared.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResponse handleBindException(BindException exception,
                                             HttpServletRequest request) {
        final var errors = getErrorsMap(exception);
        return ErrorResponse.builder()
                .reason(ErrorResponse.REASON_BAD_REQUEST)
                .message(ErrorResponse.MESSAGE_INVALID_REQUEST)
                .errors(errors)
                .request(request)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ErrorResponse handleHandlerMethodValidationException(HandlerMethodValidationException exception,
                                                                HttpServletRequest request) {
        final var errors = getErrorsMap(exception);
        return ErrorResponse.builder()
                .reason(ErrorResponse.REASON_BAD_REQUEST)
                .message(ErrorResponse.MESSAGE_INVALID_REQUEST)
                .errors(errors)
                .request(request)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorResponse handleHttpMessageNotReadableException(HttpMessageNotReadableException exception,
                                                               HttpServletRequest request) {
        return ErrorResponse.builder()
                .reason(ErrorResponse.REASON_BAD_REQUEST)
                .message(ErrorResponse.MESSAGE_MISSING_REQUEST_BODY)
                .request(request)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(UseCaseException.EntityNotFoundException.class)
    public ErrorResponse handleEntityNotFoundException(UseCaseException.EntityNotFoundException exception,
                                                       HttpServletRequest request) {
        return ErrorResponse.builder()
                .reason(ErrorResponse.REASON_ENTITY_NOT_FOUND)
                .message(exception.getMessage())
                .request(request)
                .build();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(UseCaseException.IllegalAccessException.class)
    public ErrorResponse handleIllegalAccessException(UseCaseException.IllegalAccessException exception,
                                                      HttpServletRequest request) {
        return ErrorResponse.builder()
                .reason(ErrorResponse.REASON_ACTION_NOT_ALLOWED)
                .message(exception.getMessage())
                .request(request)
                .build();
    }

    private Map<String, String> getErrorsMap(BindException exception) {
        return exception.getFieldErrors().stream()
                .filter(fieldError -> Objects.nonNull(fieldError.getDefaultMessage()))
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }

    private static Map<String, String> getErrorsMap(HandlerMethodValidationException exception) {
        return exception.getAllValidationResults().stream()
                .map(ParameterValidationResult::getResolvableErrors)
                .flatMap(List::stream)
                .map(messageSourceResolvable -> (FieldError) messageSourceResolvable)
                .filter(fieldError -> Objects.nonNull(fieldError.getDefaultMessage()))
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
    }
}
