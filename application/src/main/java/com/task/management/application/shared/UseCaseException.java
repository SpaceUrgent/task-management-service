package com.task.management.application.shared;

public class UseCaseException extends Exception {
    protected UseCaseException(String message) {
        super(message);
    }

    public static class EntityNotFoundException extends UseCaseException {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }

    public static class IllegalAccessException extends UseCaseException {
        public IllegalAccessException(String message) {
            super(message);
        }
    }
}
