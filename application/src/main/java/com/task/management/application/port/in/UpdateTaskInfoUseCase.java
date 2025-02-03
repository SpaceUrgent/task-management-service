package com.task.management.application.port.in;

import com.task.management.application.dto.TaskDetailsDTO;
import com.task.management.application.dto.UpdateTaskInfoDTO;
import com.task.management.application.exception.EntityNotFoundException;
import com.task.management.application.exception.InsufficientPrivilegesException;
import com.task.management.application.model.TaskId;
import com.task.management.application.model.UserId;

public interface UpdateTaskInfoUseCase {
    TaskDetailsDTO updateTask(UserId currentUser, TaskId taskId, UpdateTaskInfoDTO updateTaskInfoDTO) throws InsufficientPrivilegesException, EntityNotFoundException;
}
