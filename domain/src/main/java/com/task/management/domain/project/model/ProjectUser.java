package com.task.management.domain.project.model;

import lombok.Builder;

import static com.task.management.domain.common.Validation.parameterRequired;

public record ProjectUser(
        ProjectUserId id,
        String email,
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
