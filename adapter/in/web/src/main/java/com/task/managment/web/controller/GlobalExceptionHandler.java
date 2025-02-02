package com.task.managment.web.controller;

import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.managment.web.dto.ErrorDTO;
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
    public ErrorDTO handleBindException(BindException exception,
                                        HttpServletRequest request) {
        final var errors = getErrorsMap(exception);
        return ErrorDTO.builder()
                .reason("Bad request")
                .message("Request validation error")
                .errors(errors)
                .request(request)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HandlerMethodValidationException.class)
    public ErrorDTO handleHandlerMethodValidationException(HandlerMethodValidationException exception,
                                                           HttpServletRequest request) {
        final var errors = getErrorsMap(exception);
        return ErrorDTO.builder()
                .reason("Bad request")
                .message("Request validation error")
                .errors(errors)
                .request(request)
                .build();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ErrorDTO handleHttpMessageNotReadableException(HttpMessageNotReadableException exception,
                                                          HttpServletRequest request) {
        return ErrorDTO.builder()
                .reason("Bad request")
                .message("Required request body is missing")
                .request(request)
                .build();
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EntityNotFoundException.class)
    public ErrorDTO handleEntityNotFoundException(EntityNotFoundException exception,
                                                  HttpServletRequest request) {
        return ErrorDTO.builder()
                .reason("Entity not found")
                .message(exception.getMessage())
                .request(request)
                .build();
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(InsufficientPrivilegesException.class)
    public ErrorDTO handleInsufficientPrivilegesException(InsufficientPrivilegesException exception,
                                                          HttpServletRequest request) {
        return ErrorDTO.builder()
                .reason("Action not allowed")
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
