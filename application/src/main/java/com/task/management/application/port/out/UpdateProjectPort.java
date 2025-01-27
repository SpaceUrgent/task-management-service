package com.task.management.application.port.out;

import com.task.management.application.model.Project;
import com.task.management.application.model.ProjectId;

public interface UpdateProjectPort {
    Project update(ProjectId id, UpdateProjectCommand command);

    record UpdateProjectCommand(String title, String description){}
}
