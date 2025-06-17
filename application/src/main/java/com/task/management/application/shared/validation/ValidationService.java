package com.task.management.application.shared.validation;

import com.task.management.application.shared.annotation.AppComponent;
import com.task.management.domain.shared.validation.ValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.List;

import static com.task.management.domain.shared.validation.Validation.parameterRequired;

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
        List<String> errors = constraintViolations.stream()
                .map(ConstraintViolation::getMessage)
                .toList();
        throw new ValidationException(errors);
    }
}
