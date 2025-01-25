package com.task.management.application.model;

import lombok.Builder;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.requireNonNull;

public record ProjectDetails(Project project,
                             User owner,
                             List<User> members) {

    @Builder
    public ProjectDetails(Project project,
                          User owner,
                          List<User> members) {
        this.project = requireNonNull(project, "Project is required");
        this.owner = requireNonNull(owner, "Owner is required");
        this.members = Collections.unmodifiableList(requireNonNull(members));
    }
}
