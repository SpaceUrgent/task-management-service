package com.task.management.application.project;

import com.task.management.application.shared.UseCaseException;

public class UpdateTaskStatusException extends UseCaseException {
    public UpdateTaskStatusException(String message) {
        super(message);
    }
}
