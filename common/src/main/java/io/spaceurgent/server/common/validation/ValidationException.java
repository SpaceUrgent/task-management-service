package io.spaceurgent.server.common.validation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ValidationException extends RuntimeException {
    private final Map<String, List<String>> validationErrors;

    public ValidationException(Map<String, List<String>> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public Map<String, List<String>> getValidationErrors() {
        return Collections.unmodifiableMap(validationErrors);
    }
}
