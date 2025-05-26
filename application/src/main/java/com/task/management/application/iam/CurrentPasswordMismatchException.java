package com.task.management.application.iam;

import com.task.management.application.common.UseCaseException;

public class CurrentPasswordMismatchException extends UseCaseException {
    public CurrentPasswordMismatchException(String message) {
        super(message);
    }
}
