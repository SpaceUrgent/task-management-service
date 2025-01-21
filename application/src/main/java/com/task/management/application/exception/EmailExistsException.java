package com.task.management.application.exception;

public class EmailExistsException extends Exception {

    public EmailExistsException(String message) {
        super(message);
    }
}
