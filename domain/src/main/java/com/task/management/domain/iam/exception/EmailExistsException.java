package com.task.management.domain.iam.exception;

import com.task.management.domain.common.Email;
import com.task.management.domain.common.UseCaseException;

public class EmailExistsException extends UseCaseException {

    public EmailExistsException(Email email) {
        super("User with email '%s' exists".formatted(email.value()));
    }
}
