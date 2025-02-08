package com.task.management.application.project.port.out;

import com.task.management.application.project.model.ProjectUser;

import java.util.Optional;

public interface FindProjectUserByEmailPort {
    Optional<ProjectUser> find(String email);
}
