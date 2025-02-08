package com.task.management.application.iam.exception;

import com.task.management.application.common.UseCaseException;

public class EmailExistsException extends UseCaseException {

    public EmailExistsException(String message) {
        super(message);
    }
}
