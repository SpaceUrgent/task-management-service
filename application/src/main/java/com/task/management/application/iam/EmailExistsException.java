package com.task.management.application.iam;

import com.task.management.application.common.UseCaseException;
import com.task.management.domain.common.model.Email;

public class EmailExistsException extends UseCaseException {

    public EmailExistsException(Email email) {
        super("User with email '%s' exists".formatted(email.value()));
    }
}
