package com.task.management.domain.project.port.out;

import com.task.management.domain.project.model.ProjectUser;

import java.util.Optional;

public interface FindProjectUserByEmailPort {
    Optional<ProjectUser> find(String email);
}
