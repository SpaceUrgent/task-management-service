package com.task.management.domain.project.port.in;

import com.task.management.domain.common.UseCaseException;
import com.task.management.domain.project.model.ProjectId;
import com.task.management.domain.project.model.ProjectUserId;
import com.task.management.domain.project.port.in.command.CreateTaskCommand;

public interface CreateTaskUseCase {
    void createTask(ProjectUserId actorId, ProjectId projectId, CreateTaskCommand command) throws UseCaseException;;
}
