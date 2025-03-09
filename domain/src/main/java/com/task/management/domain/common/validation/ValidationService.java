package com.task.management.domain.common.validation;

import com.task.management.domain.common.annotation.AppComponent;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

@AppComponent
public class ValidationService {
    private final Validator validator;

    public ValidationService(Validator validator) {
        try (var validatorFactory = Validation.buildDefaultValidatorFactory()) {
            this.validator = validatorFactory.getValidator();
        }
    }

    public <T> void validate(final T target) {
        parameterRequired(target, "Validation target");
        final var constraintViolations = validator.validate(target);
        if (constraintViolations.isEmpty()) return;
        throw new ValidationException(constraintViolations);
    }
}
