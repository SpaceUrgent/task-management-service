package com.task.management.application.port.out;

import com.task.management.application.model.Project;

public interface ProjectRepository {
    Project add(Project project);
}
