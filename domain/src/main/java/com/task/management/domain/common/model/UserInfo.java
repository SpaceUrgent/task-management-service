package com.task.management.domain.common.model;

import com.task.management.domain.common.model.objectvalue.Email;
import com.task.management.domain.common.model.objectvalue.UserId;
import lombok.Builder;

import static com.task.management.domain.common.validation.Validation.emailRequired;
import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

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
