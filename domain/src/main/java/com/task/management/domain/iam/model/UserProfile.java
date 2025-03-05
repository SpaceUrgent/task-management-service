package com.task.management.domain.iam.model;

import lombok.Builder;

import static com.task.management.domain.common.Validation.parameterRequired;

public record UserProfile(
        UserId id,
        String email,
        String firstName,
        String lastName
) {

    @Builder
    public UserProfile {
        parameterRequired(id, "Id");
        parameterRequired(email, "Email");
        parameterRequired(firstName, "First name");
        parameterRequired(lastName, "Last name");
    }
}
