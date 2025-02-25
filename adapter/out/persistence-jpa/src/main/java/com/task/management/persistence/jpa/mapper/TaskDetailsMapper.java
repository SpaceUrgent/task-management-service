package com.task.management.persistence.jpa.mapper;

import com.task.management.application.project.model.ProjectId;
import com.task.management.application.project.model.TaskDetails;
import com.task.management.application.project.model.TaskId;
import com.task.management.application.project.model.TaskStatus;
import com.task.management.persistence.jpa.entity.TaskEntity;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class TaskDetailsMapper {
    private final ProjectUserMapper projectUserMapper;

    public TaskDetails toModel(TaskEntity entity) {
        requireNonNull(entity, "Task entity is required");
        return TaskDetails.builder()
                .id(new TaskId(entity.getId()))
                .createdAt(entity.getCreatedAt())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .status(new TaskStatus(entity.getStatus()))
                .projectId(new ProjectId(entity.getProject().getId()))
                .owner(projectUserMapper.toModel(entity.getOwner()))
                .assignee(projectUserMapper.toModel(entity.getAssignee()))
                .build();
    }
}
