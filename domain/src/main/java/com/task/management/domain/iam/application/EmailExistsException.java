package com.task.management.domain.iam.application;

import com.task.management.domain.common.model.Email;
import com.task.management.domain.common.application.UseCaseException;

public class EmailExistsException extends UseCaseException {

    public EmailExistsException(Email email) {
        super("User with email '%s' exists".formatted(email.value()));
    }
}
