package com.task.management.domain.iam.exception;

import com.task.management.domain.common.UseCaseException;

public class EmailExistsException extends UseCaseException {

    public EmailExistsException(String message) {
        super(message);
    }
}
