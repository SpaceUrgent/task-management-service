package com.task.management.application.project.port.out;

import com.task.management.application.project.model.Project;
import com.task.management.application.project.model.ProjectId;

import java.util.Optional;

public interface FindProjectByIdPort {
    Optional<Project> find(ProjectId id);
}
