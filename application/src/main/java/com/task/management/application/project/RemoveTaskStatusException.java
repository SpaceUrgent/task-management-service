package com.task.management.application.project;

import com.task.management.application.common.UseCaseException;

public class RemoveTaskStatusException extends UseCaseException {

    public RemoveTaskStatusException(String message) {
        super(message);
    }
}
