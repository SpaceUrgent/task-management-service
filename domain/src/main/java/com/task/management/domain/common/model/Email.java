package com.task.management.domain.common.model;

import com.task.management.domain.common.validation.ValidationException;

import java.io.Serializable;
import java.util.regex.Pattern;

import static com.task.management.domain.common.validation.Validation.notBlank;

public record Email(String value) implements Serializable {
    public static final String VALID_EMAIL_REGEXP = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    public static final Pattern VALID_EMAIL_PATTERN = Pattern.compile(VALID_EMAIL_REGEXP, Pattern.CASE_INSENSITIVE);

    public Email {
        notBlank(value,"Email value");
        var matcher = VALID_EMAIL_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new ValidationException("Invalid email value");
        }
    }
}
