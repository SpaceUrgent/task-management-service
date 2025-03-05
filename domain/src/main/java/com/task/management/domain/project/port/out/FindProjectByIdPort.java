package com.task.management.domain.project.port.out;

import com.task.management.domain.project.model.Project;
import com.task.management.domain.project.model.ProjectId;

import java.util.Optional;

public interface FindProjectByIdPort {
    Optional<Project> find(ProjectId id);
}
