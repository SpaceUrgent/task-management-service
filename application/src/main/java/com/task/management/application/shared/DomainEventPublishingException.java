package com.task.management.application.shared;

public class DomainEventPublishingException extends RuntimeException {

    public DomainEventPublishingException(String message) {
        super(message);
    }

    public DomainEventPublishingException(String message, Throwable cause) {
      super(message, cause);
    }
}
