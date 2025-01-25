package com.task.management.application.port.out;

import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;

import java.util.Optional;

public interface FindProjectPort {
    Optional<Project> findById(ProjectId id);
}
