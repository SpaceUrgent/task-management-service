package com.task.management.domain.common;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

public class ValidationService {
    private final Validator validator;

    public ValidationService(Validator validator) {
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    public <T> void validate(final T target) {
        final var constraintViolations = validator.validate(target);
        if (constraintViolations.isEmpty()) return;
        throw new ValidationException(constraintViolations);
    }
}
