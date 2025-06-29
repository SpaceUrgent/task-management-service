package com.task.management.application.iam;

import com.task.management.application.shared.UseCaseException;

public class CurrentPasswordMismatchException extends UseCaseException {
    public CurrentPasswordMismatchException(String message) {
        super(message);
    }
}
