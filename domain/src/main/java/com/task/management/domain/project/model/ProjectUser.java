package com.task.management.domain.project.model;

import com.task.management.domain.common.Email;
import lombok.Builder;

import static com.task.management.domain.common.validation.Validation.parameterRequired;

public record ProjectUser(
        ProjectUserId id,
        Email email,
        String firstName,
        String lastName
) {

    public static ProjectUser withId(ProjectUserId id) {
        return ProjectUser.builder().id(id).build();
    }

    @Builder
    public ProjectUser {
        parameterRequired(id, "Project user id");
    }
}
