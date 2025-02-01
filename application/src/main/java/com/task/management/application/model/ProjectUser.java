package com.task.management.application.model;

import lombok.Builder;

import static java.util.Objects.requireNonNull;

public record ProjectUser(
        UserId id,
        String email,
        String firstName,
        String lastName
) {

    public static ProjectUser withId(UserId id) {
        return ProjectUser.builder().id(id).build();
    }

    @Builder
    public ProjectUser {
        requireNonNull(id, "User id is required");
    }
}
