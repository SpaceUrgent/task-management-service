package com.task.management.application.service.mapper;

import com.task.management.application.dto.TaskDetailsDTO;
import com.task.management.application.model.Task;
import lombok.RequiredArgsConstructor;

import static java.util.Objects.requireNonNull;

@RequiredArgsConstructor
public class TaskDetailsMapper {
    private final ProjectUserMapper projectUserMapper;

    public TaskDetailsDTO toDTO(Task model) {
        requireNonNull(model, "Model is required");
        return TaskDetailsDTO.builder()
                .id(model.getId().value())
                .title(model.getTitle())
                .description(model.getDescription())
                .status(model.getStatus().name())
                .owner(projectUserMapper.toDTO(model.getOwner()))
                .assignee(projectUserMapper.toDTO(model.getOwner()))
                .build();
    }
}
