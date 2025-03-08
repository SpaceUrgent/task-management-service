package com.task.management.domain.iam.model;

import com.task.management.domain.common.Email;
import lombok.Builder;

import static com.task.management.domain.common.validation.Validation.emailRequired;
import static com.task.management.domain.common.validation.Validation.notBlank;
import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record UserProfile(
        UserId id,
        Email email,
        String firstName,
        String lastName
) {

    @Builder
    public UserProfile {
        parameterRequired(id, "Id");
        emailRequired(email);
        notBlank(firstName, "First name");
        notBlank(lastName, "Last name");
    }
}
