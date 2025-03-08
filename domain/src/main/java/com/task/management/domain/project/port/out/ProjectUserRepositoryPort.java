package com.task.management.domain.project.port.out;

import com.task.management.domain.common.Email;
import com.task.management.domain.project.model.ProjectUser;
import com.task.management.domain.project.model.ProjectUserId;

import java.util.Optional;

public interface ProjectUserRepositoryPort {
    Optional<ProjectUser> find(ProjectUserId id);

    Optional<ProjectUser> find(Email email);
}
