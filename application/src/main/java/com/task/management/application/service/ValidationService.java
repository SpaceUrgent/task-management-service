package com.task.management.application.service;

import com.task.management.application.exception.ValidationException;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import static java.util.Objects.requireNonNull;

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

    public static void userIdRequired(UserId id) {
        requireNonNull(id, "User id is required");
    }

    public static void projectIdRequired(ProjectId projectId) {
        requireNonNull(projectId, "Project id is required");
    }
}
