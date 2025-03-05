package com.task.management.domain.common;

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

    public ValidationException(String error) {
        this.errors = List.of(error);
    }
}
