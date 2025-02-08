package com.task.management.application.project.port.out;

import com.task.management.application.project.model.ProjectUser;
import com.task.management.application.project.model.ProjectUserId;

import java.util.Optional;

public interface FindProjectUserByIdPort {
    Optional<ProjectUser> find(ProjectUserId id);
}
