package com.task.management.domain.common;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Validation {
    private Validation() {
    }

    private static final Pattern VALID_EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");

    public static <T> T parameterRequired(T parameterValue, String parameterName) {
        return Objects.requireNonNull(parameterValue, "%s is required".formatted(parameterName));
    }

    public static String emailRequired(String email) {
        parameterRequired(email, "Email");
        if (VALID_EMAIL_PATTERN.matcher(email).matches()) {
            return email;
        }
        throw new ValidationException("Invalid email");
    }
}
