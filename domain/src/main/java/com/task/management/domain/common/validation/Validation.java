package com.task.management.domain.common.validation;

import com.task.management.domain.common.event.DomainEvent;
import com.task.management.domain.common.model.objectvalue.Email;
import com.task.management.domain.common.model.objectvalue.UserId;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Objects;

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

    public static LocalDate presentOrFuture(LocalDate localDate, String argumentName) {
        parameterRequired(localDate, argumentName);
        if (LocalDate.now().isAfter(localDate)) {
            throw new ValidationException("%s must be present or future date".formatted(argumentName));
        }
        return localDate;
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
