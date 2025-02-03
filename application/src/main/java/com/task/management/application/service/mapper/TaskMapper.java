package com.task.management.application.service.mapper;

import com.task.management.application.dto.TaskDTO;
import com.task.management.application.model.Task;

import static java.util.Objects.requireNonNull;

public class TaskMapper {
    private final ProjectUserMapper projectUserMapper;

    TaskMapper(ProjectUserMapper projectUserMapper) {
        this.projectUserMapper = projectUserMapper;
    }

    public TaskDTO toDTO(Task task) {
        requireNonNull(task, "Task is required");
        return TaskDTO.builder()
                .id(task.getId().value())
                .title(task.getTitle())
                .status(task.getStatus().name())
                .assignee(projectUserMapper.toDTO(task.getAssignee()))
                .build();
    }
}
