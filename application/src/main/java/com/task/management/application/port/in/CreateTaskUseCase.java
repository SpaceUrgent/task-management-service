package com.task.management.application.port.in;

import com.task.management.application.dto.CreateTaskDTO;
import com.task.management.application.dto.TaskDTO;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.ProjectId;
import com.task.management.application.model.UserId;

public interface CreateTaskUseCase {
    TaskDTO createTask(UserId currentUser, ProjectId projectId, CreateTaskDTO createTaskDTO) throws InsufficientPrivilegesException, EntityNotFoundException;
}
