package com.task.management.domain.shared.validation;

//import jakarta.validation.ConstraintViolation;
import lombok.Getter;

import java.util.List;

@Getter
public class ValidationException extends RuntimeException {
    private final List<String> errors;

//    public <T> ValidationException(Set<ConstraintViolation<T>> constraintViolations) {
//        this.errors = constraintViolations.stream()
//                .map(ConstraintViolation::getMessage)
//                .toList();
//    }

    public ValidationException(String error) {
        this(List.of(error));
    }

    public ValidationException(List<String> errors) {
        this.errors = errors;
    }
}
