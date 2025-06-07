package com.task.management.application.project.port.in;

import com.task.management.application.common.UseCaseException;
import com.task.management.application.project.command.CreateTaskCommand;
import com.task.management.domain.common.model.objectvalue.UserId;
import com.task.management.domain.common.model.objectvalue.ProjectId;

public interface CreateTaskUseCase {
    void createTask(UserId actorId, ProjectId projectId, CreateTaskCommand command) throws UseCaseException;;
}
