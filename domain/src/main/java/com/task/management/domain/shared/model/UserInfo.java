package com.task.management.domain.shared.model;

import com.task.management.domain.shared.model.objectvalue.Email;
import com.task.management.domain.shared.model.objectvalue.UserId;
import lombok.Builder;

import static com.task.management.domain.shared.validation.Validation.emailRequired;
import static com.task.management.domain.shared.validation.Validation.notBlank;
import static com.task.management.domain.shared.validation.Validation.parameterRequired;

public record UserInfo(
        UserId id,
        Email email,
        String firstName,
        String lastName
) {

    @Builder
    public UserInfo {
        parameterRequired(id, "Id");
        emailRequired(email);
        notBlank(firstName, "First name");
        notBlank(lastName, "Last name");
    }

    public String fullName() {
        return "%s %s".formatted(firstName, lastName);
    }
}
