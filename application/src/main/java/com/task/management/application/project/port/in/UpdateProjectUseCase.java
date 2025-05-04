package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.command.AddTaskStatusCommand;
import com.task.management.application.project.command.UpdateProjectCommand;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.project.model.objectvalue.ProjectId;

public interface UpdateProjectUseCase {
    void updateProject(UserId actorId, ProjectId projectId, UpdateProjectCommand command) throws UseCaseException;

    void addTaskStatus(UserId actorId, ProjectId projectId, AddTaskStatusCommand command) throws UseCaseException;

    void removeTaskStatus(UserId actorId, ProjectId projectId, String statusName) throws UseCaseException;
}
