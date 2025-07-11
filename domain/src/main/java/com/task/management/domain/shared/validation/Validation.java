package com.task.management.domain.shared.validation;

import com.task.management.domain.shared.event.DomainEvent;
import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public final class Validation {
    private Validation() {
    }

    public static <T> T parameterRequired(T parameterValue, String argumentName) {
        return Objects.requireNonNull(parameterValue, "%s is required".formatted(argumentName));
    }

    public static UserId actorIdRequired(UserId actorId) {
        return parameterRequired(actorId, "Actor id");
    }

    public static String notBlank(String argumentValue, String argumentName) {
        parameterRequired(argumentValue, argumentName);
        if (!argumentValue.isBlank()) {
            return argumentValue;
        }
        throw new ValidationException("%s must be not blank".formatted(argumentName));
    }

    public static LocalDate presentOrFuture(LocalDate argument, String argumentName) {
        Optional.ofNullable(argument).ifPresent(date -> {
            if (LocalDate.now().isAfter(argument)) {
                throw new ValidationException("%s must be present or future date".formatted(argumentName));
            }
        });
        return argument;
    }

    public static Email emailRequired(Email email) {
        return parameterRequired(email, "Email");
    }

    public static <T extends Collection<R>, R> T notEmpty(T collection, String argumentName) {
        parameterRequired(collection, argumentName);
        if (!collection.isEmpty()) {
            return collection;
        }
        throw new ValidationException("%s must be not empty".formatted(argumentName));
    }

    public static DomainEvent eventRequired(DomainEvent event) {
        return parameterRequired(event, "Event");
    }
}
