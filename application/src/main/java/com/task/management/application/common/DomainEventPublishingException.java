package com.task.management.application.common;

public class DomainEventPublishingException extends RuntimeException {

    public DomainEventPublishingException(String message) {
        super(message);
    }

    public DomainEventPublishingException(String message, Throwable cause) {
      super(message, cause);
    }
}
