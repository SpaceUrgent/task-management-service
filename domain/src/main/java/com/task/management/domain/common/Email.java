package com.task.management.domain.common;

import com.task.management.domain.common.validation.ValidationException;

import java.util.regex.Pattern;

import static com.task.management.domain.common.validation.Validation.notBlank;

public record Email(String value) {
    public static final Pattern VALID_EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$", Pattern.CASE_INSENSITIVE);

    public Email {
        notBlank(value,"Email value");
        var matcher = VALID_EMAIL_PATTERN.matcher(value);
        if (!matcher.matches()) {
            throw new ValidationException("Invalid email value");
        }
    }
}
