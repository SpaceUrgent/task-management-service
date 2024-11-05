package io.spaceurgent.server.common.validation;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.util.Set;
import java.util.stream.Collectors;

public class ValidationService {

    private final Validator validator;

    public ValidationService(Validator validator) {
        this.validator = validator;
    }

    public ValidationService() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            this.validator = factory.getValidator();
        }
    }

    public <T> void validate(T target) {
        Set<ConstraintViolation<T>> constraintViolations = this.validator.validate(target);
        if (constraintViolations.isEmpty()) return;
        final var errorsMap = constraintViolations.stream()
                .collect(Collectors.groupingBy(
                        violation -> violation.getPropertyPath().toString(),
                        Collectors.mapping(ConstraintViolation::getMessage, Collectors.toList())
                ));
        throw new ValidationException(errorsMap);
    }
}
