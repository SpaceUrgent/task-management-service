package com.task.management.application.project.port.out;

import com.task.management.application.project.model.Project;

public interface SaveProjectPort {
    Project save(Project project);
}
