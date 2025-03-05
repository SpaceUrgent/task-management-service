package com.task.management.domain.project.port.out;

import com.task.management.domain.project.model.Project;

public interface UpdateProjectPort {
    Project update(Project project);
}
