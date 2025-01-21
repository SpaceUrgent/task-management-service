package com.task.management.application.exception;

import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.List;
import java.util.Set;

@Getter
public class ValidationException extends RuntimeException {
    private final List<String> errors;

    public <T> ValidationException(Set<ConstraintViolation<T>> constraintViolations) {
        this.errors = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
    }
}
